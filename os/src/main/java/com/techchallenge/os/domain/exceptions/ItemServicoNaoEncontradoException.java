package com.techchallenge.os.domain.exceptions;

import java.util.UUID;

public class ItemServicoNaoEncontradoException extends DomainException {

    public ItemServicoNaoEncontradoException(UUID id) {
        super("Item de Serviço não encontrado com ID: " + id, "ITEM_SERVICO_NAO_ENCONTRADO");
    }
}
