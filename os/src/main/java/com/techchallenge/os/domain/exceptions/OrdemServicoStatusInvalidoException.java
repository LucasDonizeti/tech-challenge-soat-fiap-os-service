package com.techchallenge.os.domain.exceptions;

public class OrdemServicoStatusInvalidoException extends DomainException {

    public OrdemServicoStatusInvalidoException(String message) {
        super(message, "ORDEM_SERVICO_STATUS_INVALIDO");
    }
}
