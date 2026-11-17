package com.techchallenge.os.domain.exceptions;

public class ValidacaoServicoException extends DomainException {

    public ValidacaoServicoException(String message) {
        super(message, "VALIDACAO_SERVICO");
    }

    public ValidacaoServicoException(String message, Throwable cause) {
        super(message, "VALIDACAO_SERVICO");
        initCause(cause);
    }
}
