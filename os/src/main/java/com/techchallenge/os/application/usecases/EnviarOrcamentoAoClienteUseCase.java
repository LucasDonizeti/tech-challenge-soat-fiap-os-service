package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.commands.EnviarOrcamentoAoClienteCommand;
import com.techchallenge.os.application.gateway.ports.input.EnviarOrcamentoAoClienteInput;
import com.techchallenge.os.application.gateway.ports.output.ClienteGateway;
import com.techchallenge.os.application.gateway.ports.output.OsEventPublisher;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.events.OrcamentoProntoEvent;
import com.techchallenge.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.os.domain.exceptions.ValidacaoClienteException;
import com.techchallenge.os.domain.models.OrdemServico;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class EnviarOrcamentoAoClienteUseCase implements EnviarOrcamentoAoClienteInput {
    
    private final OrdemServicoGateway ordemServicoGateway;
    private final OrdemServicoResponseAssembler ordemServicoResponseAssembler;
    private final ClienteGateway clienteGateway;
    private final OsEventPublisher osEventPublisher;

    public OrdemServicoResponse execute(EnviarOrcamentoAoClienteCommand command) {
        log.info("Enviando orçamento ao cliente: ordemServicoId={}", command.getOrdemServicoId());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoGateway.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getOrdemServicoId()));

        ordemServico.validarEnvioOrcamentoAoCliente();
        var cliente = clienteGateway.findById(ordemServico.getCliente())
                .orElseThrow(() -> new ValidacaoClienteException(
                        "Cliente não encontrado com ID: " + ordemServico.getCliente()));
        String identificationType = cliente.getCpf() != null && !cliente.getCpf().isBlank() ? "CPF" : "CNPJ";
        String identificationNumber = identificationType.equals("CPF") ? cliente.getCpf() : cliente.getCnpj();
        if (cliente.getEmail() == null || cliente.getEmail().isBlank()
                || identificationNumber == null || identificationNumber.isBlank()) {
            throw new ValidacaoClienteException(
                    "O cliente precisa ter e-mail e documento válidos para gerar cobrança PIX.");
        }

        // Enviar orçamento ao cliente (validações são feitas no domínio)
        OrcamentoProntoEvent event = ordemServico.enviarOrcamentoAoCliente(
                cliente.getEmail(),
                identificationType,
                identificationNumber.replaceAll("\\D", "")
        );
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoGateway.save(ordemServico);

        osEventPublisher.publishOrcamentoPronto(event);
        
        log.info("Orçamento enviado ao cliente com sucesso: ID={}, Status={}", 
                savedOrdemServico.getId(), savedOrdemServico.getStatus());

        return ordemServicoResponseAssembler.montar(savedOrdemServico);
    }
}
