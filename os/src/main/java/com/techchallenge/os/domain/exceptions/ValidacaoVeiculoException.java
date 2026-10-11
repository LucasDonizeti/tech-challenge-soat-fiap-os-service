package com.techchallenge.os.domain.exceptions;

public class ValidacaoVeiculoException extends DomainException {

    public ValidacaoVeiculoException(String message) {
        super(message, "VALIDACAO_VEICULO");
    }

    public ValidacaoVeiculoException(String message, Throwable cause) {
        super(message, "VALIDACAO_VEICULO");
        initCause(cause);
    }
}
