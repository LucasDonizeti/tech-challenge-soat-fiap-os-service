package com.techchallenge.os.domain.exceptions;

public class ValidacaoOrdemServicoException extends DomainException {

    public ValidacaoOrdemServicoException(String message) {
        super(message, "VALIDACAO_ORDEM_SERVICO");
    }

    public ValidacaoOrdemServicoException(String message, Throwable cause) {
        super(message, "VALIDACAO_ORDEM_SERVICO");
        initCause(cause);
    }
}
