package com.techchallenge.os.domain.exceptions;

public class ValidacaoMROException extends DomainException {

    public ValidacaoMROException(String message) {
        super(message, "VALIDACAO_MRO");
    }

    public ValidacaoMROException(String message, Throwable cause) {
        super(message, "VALIDACAO_MRO");
        initCause(cause);
    }
}