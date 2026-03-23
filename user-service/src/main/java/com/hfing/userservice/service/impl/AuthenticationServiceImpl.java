package com.hfing.userservice.service.impl;

import com.hfing.userservice.dto.TokenDetails;
import com.hfing.userservice.dto.request.IntrospectRequest;
import com.hfing.userservice.dto.request.LoginRequest;
import com.hfing.userservice.dto.response.IntrospectResponse;
import com.hfing.userservice.dto.response.LoginResponse;
import com.hfing.userservice.entity.RedisToken;
import com.hfing.userservice.entity.User;
import com.hfing.userservice.exception.ErrorCode;
import com.hfing.userservice.exception.UserServiceException;
import com.hfing.userservice.repository.UserRepository;
import com.hfing.userservice.service.AuthenticationService;
import com.hfing.userservice.service.JwtService;
import com.hfing.userservice.service.RedisTokenService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION-SERVICE")
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RedisTokenService redisTokenService;

    @Override
    public LoginResponse login(LoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.email(), request.password());

        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        User user = (User) authenticate.getPrincipal();
        if (user == null) {
            throw new UserServiceException(ErrorCode.USER_NOT_FOUND);
        }

        Set<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        String accessToken = jwtService.generateAccessToken(user.getId(), roles);
        TokenDetails refreshToken = jwtService.generateRefreshToken(user.getId());

        RedisToken redisToken = RedisToken.builder()
                .jwtId(refreshToken.jwtId())      // UUID của token
                .userId(user.getId())              // User ID để query sau này
                .expiration(refreshToken.ttlSeconds()) // TTL = 14 ngày
                .build();

        redisTokenService.saveToken(redisToken);



        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.value())
                .roles(roles)
                .build();
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {

        try {
            SignedJWT signedJWT = jwtService.validateToken(refreshToken);
            String userId = signedJWT.getJWTClaimsSet().getSubject();

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserServiceException(ErrorCode.USER_NOT_FOUND));

            Set<String> roles = user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            String newAccessToken = jwtService.generateAccessToken(userId, roles);

            return LoginResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken)
                    .roles(roles)
                    .build();

        } catch (ParseException | JOSEException e) {
            throw new UserServiceException(ErrorCode.TOKEN_INVALID);
        }
    }

    @Override
    public void logout(String refreshToken) throws ParseException, JOSEException {
        // 1. Validate refresh token có tồn tại không
        if (refreshToken == null) {
            throw new UserServiceException(ErrorCode.MISSING_LOGOUT_INFO);
        }

        // 2. Lấy thông tin user từ SecurityContext (từ access token)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null)
            throw new UserServiceException(ErrorCode.TOKEN_INVALID);
        String userId = authentication.getName();

        // 3. Validate refresh token và lấy thông tin
        SignedJWT signedRefreshToken = jwtService.validateToken(refreshToken);

        String refreshUserId = signedRefreshToken.getJWTClaimsSet().getSubject();
        String refreshJwtId = signedRefreshToken.getJWTClaimsSet().getJWTID();

        // 4. Verify userId từ access token và refresh token phải giống nhau
        // Tránh trường hợp user A dùng access token của mình + refresh token của user B
        if (!userId.equals(refreshUserId)) {
            throw new UserServiceException(ErrorCode.TOKEN_INVALID);
        }

        // 5. Xóa refresh token khỏi Redis
        // Refresh token đã được lưu vào Redis khi login (Lesson 4.14)
        redisTokenService.deleteTokenByJwtId(refreshJwtId);

        // 6. Lấy thông tin access token từ SecurityContext
        Jwt jwt = (Jwt) authentication.getPrincipal();
        if(jwt == null)
            throw new UserServiceException(ErrorCode.TOKEN_INVALID);

        String accessJwtId = jwt.getId();
        Instant accessExpiration = jwt.getExpiresAt();

        // 7. Tính TTL còn lại của access token
        // TTL = thời gian hết hạn - thời gian hiện tại
        long ttl = ChronoUnit.SECONDS.between(
                Instant.now(),
                accessExpiration
        );

        // 8. Nếu access token còn hạn → lưu vào Redis blacklist
        // Nếu đã hết hạn (ttl <= 0) → không cần lưu vì token đã invalid
        if (ttl > 0) {
            redisTokenService.saveToken(
                    RedisToken.builder()
                            .jwtId(accessJwtId)
                            .userId(userId)
                            .expiration(ttl)
                            .build()
            );
        }
    }


    @Override
    public IntrospectResponse introspectToken(IntrospectRequest request) {
        try {
            // 1. Validate token (verify signature và check expiration)
            SignedJWT signedJWT = jwtService.validateToken(request.token());

            // 2. Extract user ID từ token
            String userId = signedJWT.getJWTClaimsSet().getSubject();

            // 3. Extract roles từ token
            Set<String> roles = extractRoles(signedJWT.getJWTClaimsSet().getClaim("roles"));

            // 4. Return response với active=true
            return IntrospectResponse.builder()
                    .active(true)
                    .userId(userId)
                    .roles(roles)
                    .build();

        } catch (ParseException | JOSEException e) {
            // Token không hợp lệ → return active=false
            return IntrospectResponse.builder()
                    .active(false)
                    .build();
        }
    }

    // Helper method để extract roles từ JWT claim
    private Set<String> extractRoles(Object rolesClaim) {
        if (rolesClaim == null) {
            return Collections.emptySet();
        }

        // JWT library serialize array thành List (không phải Set)
        if (rolesClaim instanceof Collection<?> collection) {
            return collection.stream()
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        }

        return Collections.emptySet();
    }



}
