package com.techchallenge.os.infrastructure.web.controllers.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de PageableValidator - Infrastructure Layer")
class PageableValidatorTest {

    private final PageableValidator validator = new PageableValidator();

    @Test
    @DisplayName("Deve retornar pageable quando sort está vazio")
    void deveRetornarPageableQuandoSortEstaVazio() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Set<String> allowedFields = Set.of("nome", "dataCriacao");

        // Act
        Pageable result = validator.validate(pageable, allowedFields);

        // Assert
        assertEquals(pageable, result);
    }

    @Test
    @DisplayName("Deve manter ordenação quando campo é permitido")
    void deveManterOrdenacaoQuandoCampoEPermitido() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome"));
        Set<String> allowedFields = Set.of("nome", "dataCriacao");

        // Act
        Pageable result = validator.validate(pageable, allowedFields);

        // Assert
        assertEquals(0, result.getPageNumber());
        assertEquals(10, result.getPageSize());
        assertEquals("nome", result.getSort().getOrderFor("nome").getProperty());
    }

    @Test
    @DisplayName("Deve remover ordenação quando campo não é permitido")
    void deveRemoverOrdenacaoQuandoCampoNaoEPermitido() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("campoInvalido"));
        Set<String> allowedFields = Set.of("nome", "dataCriacao");

        // Act
        Pageable result = validator.validate(pageable, allowedFields);

        // Assert
        assertEquals(0, result.getPageNumber());
        assertEquals(10, result.getPageSize());
        assertTrue(result.getSort().isEmpty());
    }

    @Test
    @DisplayName("Deve manter apenas campos permitidos quando tem múltiplos campos")
    void deveManterApenasCamposPermitidosQuandoTemMultiplosCampos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome", "campoInvalido", "dataCriacao"));
        Set<String> allowedFields = Set.of("nome", "dataCriacao");

        // Act
        Pageable result = validator.validate(pageable, allowedFields);

        // Assert
        assertEquals(0, result.getPageNumber());
        assertEquals(10, result.getPageSize());
        assertEquals(2, result.getSort().stream().count());
        assertTrue(result.getSort().getOrderFor("nome") != null);
        assertTrue(result.getSort().getOrderFor("dataCriacao") != null);
        assertNull(result.getSort().getOrderFor("campoInvalido"));
    }

    @Test
    @DisplayName("Deve manter paginação quando ordenação é removida")
    void deveManterPaginacaoQuandoOrdenacaoERemovida() {
        // Arrange
        Pageable pageable = PageRequest.of(2, 20, Sort.by("campoInvalido"));
        Set<String> allowedFields = Set.of("nome");

        // Act
        Pageable result = validator.validate(pageable, allowedFields);

        // Assert
        assertEquals(2, result.getPageNumber());
        assertEquals(20, result.getPageSize());
        assertTrue(result.getSort().isEmpty());
    }

    @Test
    @DisplayName("Deve manter ordenação descendente quando campo é permitido")
    void deveManterOrdenacaoDescendenteQuandoCampoEPermitido() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "dataCriacao"));
        Set<String> allowedFields = Set.of("nome", "dataCriacao");

        // Act
        Pageable result = validator.validate(pageable, allowedFields);

        // Assert
        assertEquals(0, result.getPageNumber());
        assertEquals(10, result.getPageSize());
        assertEquals(Sort.Direction.DESC, result.getSort().getOrderFor("dataCriacao").getDirection());
    }

    @Test
    @DisplayName("Deve manter direção diferente para cada campo")
    void deveManterDirecaoDiferenteParaCadaCampo() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10,
                Sort.by(Sort.Direction.ASC, "nome").and(Sort.by(Sort.Direction.DESC, "dataCriacao")));
        Set<String> allowedFields = Set.of("nome", "dataCriacao");

        // Act
        Pageable result = validator.validate(pageable, allowedFields);

        // Assert
        assertEquals(Sort.Direction.ASC, result.getSort().getOrderFor("nome").getDirection());
        assertEquals(Sort.Direction.DESC, result.getSort().getOrderFor("dataCriacao").getDirection());
    }

    @Test
    @DisplayName("Deve funcionar com conjunto de campos permitidos vazio")
    void deveFuncionarComConjuntoDeCamposPermitidosVazio() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome"));
        Set<String> allowedFields = Set.of();

        // Act
        Pageable result = validator.validate(pageable, allowedFields);

        // Assert
        assertEquals(0, result.getPageNumber());
        assertEquals(10, result.getPageSize());
        assertTrue(result.getSort().isEmpty());
    }

    @Test
    @DisplayName("Deve manter paginação quando não há ordenação")
    void deveManterPaginacaoQuandoNaoHaOrdenacao() {
        // Arrange
        Pageable pageable = PageRequest.of(5, 25);
        Set<String> allowedFields = Set.of("nome", "dataCriacao");

        // Act
        Pageable result = validator.validate(pageable, allowedFields);

        // Assert
        assertEquals(5, result.getPageNumber());
        assertEquals(25, result.getPageSize());
        assertTrue(result.getSort().isEmpty());
    }
}
