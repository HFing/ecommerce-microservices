package com.hfing.userservice.service.impl;

import com.hfing.userservice.dto.request.LoginRequest;
import com.hfing.userservice.dto.response.LoginResponse;
import com.hfing.userservice.entity.User;
import com.hfing.userservice.exception.ErrorCode;
import com.hfing.userservice.exception.UserServiceException;
import com.hfing.userservice.repository.UserRepository;
import com.hfing.userservice.service.AuthenticationService;
import com.hfing.userservice.service.JwtService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION-SERVICE")
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

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
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
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



}
