package com.techchallenge.os.infrastructure.web.config.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de SecutiryConfig - Infrastructure Layer")
class SecutiryConfigTest {

    private SecutiryConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecutiryConfig();
        ReflectionTestUtils.setField(securityConfig, "jwtSecret", "mySuperSecretKeyForJWTTokenGenerationThatIsSecureEnough");
    }

    @Test
    @DisplayName("Deve criar bean JwtDecoder com secret configurada")
    void deveCriarBeanJwtDecoderComSecretConfigurada() {
        // Act
        JwtDecoder jwtDecoder = securityConfig.jwtDecoder();

        // Assert
        assertNotNull(jwtDecoder);
    }

    @Test
    @DisplayName("Deve criar bean JwtAuthenticationConverter")
    void deveCriarBeanJwtAuthenticationConverter() {
        // Act
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverter();

        // Assert
        assertNotNull(converter);
    }

    @Test
    @DisplayName("Deve converter ROLE_SERVICE para chamadas entre serviços")
    void deveConverterRoleService() {
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverter();
        var jwt = org.springframework.security.oauth2.jwt.Jwt.withTokenValue("service-token")
                .header("alg", "HS256")
                .claim("role", "SERVICE")
                .build();

        var authentication = converter.convert(jwt);

        assertNotNull(authentication);
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_SERVICE".equals(authority.getAuthority())));
    }

    @Test
    @DisplayName("Deve usar secret customizada quando configurada")
    void deveUsarSecretCustomizadaQuandoConfigurada() {
        // Arrange
        String customSecret = "myCustomSecretKey123456789";
        SecutiryConfig configCustomizada = new SecutiryConfig();
        ReflectionTestUtils.setField(configCustomizada, "jwtSecret", customSecret);

        // Act
        JwtDecoder jwtDecoder = configCustomizada.jwtDecoder();

        // Assert
        assertNotNull(jwtDecoder);
    }
}
