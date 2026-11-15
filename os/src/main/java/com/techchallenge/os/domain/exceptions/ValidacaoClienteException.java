package com.techchallenge.os.domain.exceptions;

public class ValidacaoClienteException extends DomainException {

    public ValidacaoClienteException(String message) {
        super(message, "VALIDACAO_CLIENTE");
    }

    public ValidacaoClienteException(String message, Throwable cause) {
        super(message, "VALIDACAO_CLIENTE");
        initCause(cause);
    }
}
