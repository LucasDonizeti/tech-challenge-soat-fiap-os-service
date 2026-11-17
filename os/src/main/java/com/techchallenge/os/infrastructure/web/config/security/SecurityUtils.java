package com.techchallenge.os.infrastructure.web.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class SecurityUtils {

    public static String getUsuarioCpf() {
        Jwt jwt = getJwt();
        return jwt != null ? jwt.getSubject() : null;
    }

    public static String getClienteId() {
        Jwt jwt = getJwt();
        if (jwt != null) {
            // Garante a leitura da claim customizada "clienteId"
            Object clienteIdClaim = jwt.getClaim("clienteId");
            return clienteIdClaim != null ? clienteIdClaim.toString() : null;
        }
        return null;
    }

    private static Jwt getJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        }

        return null;
    }
}
