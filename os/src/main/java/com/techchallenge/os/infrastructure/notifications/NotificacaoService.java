package com.techchallenge.os.infrastructure.notifications;

import com.techchallenge.os.domain.exceptions.NotificacaoException;

public interface NotificacaoService {
    void notificarCliente(String email, String nome, String assunto, String mensagem) throws NotificacaoException;
}
