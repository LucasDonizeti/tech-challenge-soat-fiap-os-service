package com.techchallenge.os.domain.exceptions;

public class ValidacaoEstoqueException extends DomainException {
    
    public ValidacaoEstoqueException(String message) {
        super(message, "VALIDACAO_ESTOQUE");
    }
    
    public ValidacaoEstoqueException(String message, Throwable cause) {
        super(message, "VALIDACAO_ESTOQUE");
        this.initCause(cause);
    }
}
