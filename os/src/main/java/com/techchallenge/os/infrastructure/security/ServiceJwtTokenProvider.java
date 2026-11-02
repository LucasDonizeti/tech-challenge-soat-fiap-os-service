package com.techchallenge.os.infrastructure.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class ServiceJwtTokenProvider {

    private static final long TOKEN_VALIDITY_MILLIS = 5 * 60 * 1000;
    private static final String SERVICE_NAME = "os-service";

    private final String jwtSecret;

    public ServiceJwtTokenProvider(
            @Value("${spring.security.jwt.secret}") String jwtSecret
    ) {
        this.jwtSecret = jwtSecret;
    }

    public String createToken() {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException("Configure JWT_SECRET para autenticar chamadas entre serviços.");
        }

        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + TOKEN_VALIDITY_MILLIS);

        return Jwts.builder()
                .setSubject(SERVICE_NAME)
                .claim("role", "SERVICE")
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)),
                        SignatureAlgorithm.HS256)
                .compact();
    }
}
