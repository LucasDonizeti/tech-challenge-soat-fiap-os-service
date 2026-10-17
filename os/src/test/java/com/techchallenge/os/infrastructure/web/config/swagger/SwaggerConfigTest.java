package com.techchallenge.os.infrastructure.web.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de SwaggerConfig - Infrastructure Layer")
class SwaggerConfigTest {

    private SwaggerConfig swaggerConfig;

    @BeforeEach
    void setUp() {
        swaggerConfig = new SwaggerConfig();
        ReflectionTestUtils.setField(swaggerConfig, "applicationName", "Tech Challenge Oficina");
        ReflectionTestUtils.setField(swaggerConfig, "applicationVersion", "1.0.0");
    }

    @Test
    @DisplayName("Deve criar bean OpenAPI com configuração padrão")
    void deveCriarBeanOpenAPIComConfiguracaoPadrao() {
        // Act
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        // Assert
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertNotNull(openAPI.getServers());
        assertNotNull(openAPI.getComponents());
        assertNotNull(openAPI.getSecurity());
        assertNotNull(openAPI.getTags());
    }

    @Test
    @DisplayName("Deve configurar informações do API corretamente")
    void deveConfigurarInformacoesDoAPICorretamente() {
        // Act
        OpenAPI openAPI = swaggerConfig.customOpenAPI();
        Info info = openAPI.getInfo();

        // Assert
        assertNotNull(info);
        assertEquals("Tech Challenge Oficina API", info.getTitle());
        assertEquals("1.0.0", info.getVersion());
        assertEquals("API REST para sistema de gestão de oficina mecânica com múltiplos bounded contexts", info.getDescription());
    }

    @Test
    @DisplayName("Deve configurar contato do API")
    void deveConfigurarContatoDoAPI() {
        // Act
        OpenAPI openAPI = swaggerConfig.customOpenAPI();
        Contact contact = openAPI.getInfo().getContact();

        // Assert
        assertNotNull(contact);
        assertEquals("Tech Challenge SOAT FIAP", contact.getName());
        assertEquals("techchallenge@fiap.com.br", contact.getEmail());
    }

    @Test
    @DisplayName("Deve configurar licença do API")
    void deveConfigurarLicencaDoAPI() {
        // Act
        OpenAPI openAPI = swaggerConfig.customOpenAPI();
        License license = openAPI.getInfo().getLicense();

        // Assert
        assertNotNull(license);
        assertEquals("Apache 2.0", license.getName());
        assertEquals("https://www.apache.org/licenses/LICENSE-2.0.html", license.getUrl());
    }

    @Test
    @DisplayName("Deve configurar servidor padrão")
    void deveConfigurarServidorPadrao() {
        // Act
        OpenAPI openAPI = swaggerConfig.customOpenAPI();
        List<Server> servers = openAPI.getServers();

        // Assert
        assertNotNull(servers);
        assertEquals(1, servers.size());
        assertEquals("/", servers.get(0).getUrl());
        assertEquals("Default Server URL", servers.get(0).getDescription());
    }

    @Test
    @DisplayName("Deve configurar esquema de segurança JWT Bearer")
    void deveConfigurarEsquemaDeSegurancaJWTBearer() {
        // Act
        OpenAPI openAPI = swaggerConfig.customOpenAPI();
        Components components = openAPI.getComponents();
        SecurityScheme securityScheme = components.getSecuritySchemes().get("Bearer Authentication");

        // Assert
        assertNotNull(securityScheme);
        assertEquals(SecurityScheme.Type.HTTP, securityScheme.getType());
        assertEquals("bearer", securityScheme.getScheme());
        assertEquals("JWT", securityScheme.getBearerFormat());
        assertTrue(securityScheme.getDescription().contains("JWT Bearer Token"));
    }

    @Test
    @DisplayName("Deve configurar requisito de segurança JWT")
    void deveConfigurarRequisitoDeSegurancaJWT() {
        // Act
        OpenAPI openAPI = swaggerConfig.customOpenAPI();
        List<SecurityRequirement> securityRequirements = openAPI.getSecurity();

        // Assert
        assertNotNull(securityRequirements);
        assertEquals(1, securityRequirements.size());
        assertTrue(securityRequirements.get(0).containsKey("Bearer Authentication"));
    }

    @Test
    @DisplayName("Deve configurar tags para endpoints")
    void deveConfigurarTagsParaEndpoints() {
        // Act
        OpenAPI openAPI = swaggerConfig.customOpenAPI();
        List<Tag> tags = openAPI.getTags();

        // Assert
        assertNotNull(tags);
        assertEquals(3, tags.size());

        Tag autenticacaoTag = tags.get(0);
        assertEquals("Autenticação", autenticacaoTag.getName());
        assertEquals("Endpoints de autenticação e geração de tokens JWT", autenticacaoTag.getDescription());

        Tag clienteTag = tags.get(1);
        assertEquals("Cliente", clienteTag.getName());
        assertEquals("Endpoints de gestão de clientes", clienteTag.getDescription());

        Tag osTag = tags.get(2);
        assertEquals("Ordem de Serviço", osTag.getName());
        assertEquals("Endpoints de gestão de ordens de serviço", osTag.getDescription());
    }

    @Test
    @DisplayName("Deve usar valores padrão quando propriedades não configuradas")
    void deveUsarValoresPadraoQuandoPropriedadesNaoConfiguradas() {
        // Arrange
        SwaggerConfig configSemValores = new SwaggerConfig();
        ReflectionTestUtils.setField(configSemValores, "applicationName", "Tech Challenge Oficina");
        ReflectionTestUtils.setField(configSemValores, "applicationVersion", "1.0.0");

        // Act
        OpenAPI openAPI = configSemValores.customOpenAPI();
        Info info = openAPI.getInfo();

        // Assert
        assertNotNull(info);
        assertEquals("Tech Challenge Oficina API", info.getTitle());
        assertEquals("1.0.0", info.getVersion());
    }

    @Test
    @DisplayName("Deve usar valores customizados quando propriedades configuradas")
    void deveUsarValoresCustomizadosQuandoPropriedadesConfiguradas() {
        // Arrange
        SwaggerConfig configCustomizada = new SwaggerConfig();
        ReflectionTestUtils.setField(configCustomizada, "applicationName", "API Customizada");
        ReflectionTestUtils.setField(configCustomizada, "applicationVersion", "2.0.0");

        // Act
        OpenAPI openAPI = configCustomizada.customOpenAPI();
        Info info = openAPI.getInfo();

        // Assert
        assertNotNull(info);
        assertEquals("API Customizada API", info.getTitle());
        assertEquals("2.0.0", info.getVersion());
    }
}
