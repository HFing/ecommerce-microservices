package com.hfing.userservice.configuration;

import com.hfing.userservice.service.RedisTokenService;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;


@Component
public class CustomJwtDecoder implements JwtDecoder {
    private final RedisTokenService redisTokenService;
    @Value("${jwt.secret-key}")
    private String secretKey;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    public CustomJwtDecoder(RedisTokenService redisTokenService) {
        this.redisTokenService = redisTokenService;
    }

    @PostConstruct
    public void init() {
        SecretKey key = new SecretKeySpec(secretKey.getBytes(), "HS512");
        nimbusJwtDecoder = NimbusJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            // Parse JWT để lấy jwtId
            SignedJWT signedJWT = SignedJWT.parse(token);
            String jwtId = signedJWT.getJWTClaimsSet().getJWTID();

            // Kiểm tra jwtId có trong Redis blacklist không
            // Nếu có → token đã bị thu hồi (user đã logout)
            if(redisTokenService.existsByJwtId(jwtId))
                throw new JwtException("Token is expired");

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // Nếu token không trong blacklist → decode bình thường
        return nimbusJwtDecoder.decode(token);
    }
}
