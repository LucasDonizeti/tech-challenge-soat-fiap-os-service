package com.techchallenge.os.infrastructure.web.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecutiryConfig {

    @Value("${spring.security.jwt.secret:mySuperSecretKeyForJWTTokenGenerationThatIsSecureEnough}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception { // NOSONAR
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos (sem token)
                        .requestMatchers("/v1/auth/**").permitAll()
                        .requestMatchers("/v1/admin/health", "/v1/os/health").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // Rotas de cliente — exigem token JWT com ROLE_CLIENTE
                        .requestMatchers("/api/cliente/**").hasRole("CLIENTE")

                        // Serviços podem consultar uma OS para processar o orçamento no billing-service.
                        .requestMatchers(HttpMethod.GET, "/v1/os/*").hasAnyRole("ADMIN", "SERVICE")

                        // Demais rotas de OS (gestão interna) — exigem token JWT com ROLE_ADMIN
                        .requestMatchers("/v1/os/**").hasRole("ADMIN")

                        // Rotas admin
                        .requestMatchers("/v1/admin/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    // Decodificador do Token usando HMAC SHA256 e a Secret em comum
    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec secretKey = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    // Converte a claim "role" da Lambda ("ADMIN" / "CLIENTE") no formato esperado pelo Spring ("ROLE_ADMIN", "ROLE_CLIENTE")
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String role = jwt.getClaimAsString("role");
            if (role == null || role.isBlank()) {
                return Collections.emptyList();
            }
            // Mapeia para ROLE_ADMIN ou ROLE_CLIENTE
            return List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
        });

        return converter;
    }
}
