package com.techchallenge.os.domain.exceptions;

public class NotificacaoException extends RuntimeException {

    public NotificacaoException(String message) {
        super(message);
    }

    public NotificacaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
