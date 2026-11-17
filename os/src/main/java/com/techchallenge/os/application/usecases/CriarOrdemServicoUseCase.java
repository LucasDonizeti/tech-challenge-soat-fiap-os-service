package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.commands.CriarOrdemServicoCommand;
import com.techchallenge.os.application.gateway.ports.input.CriarOrdemServicoInpuit;
import com.techchallenge.os.application.gateway.ports.output.*;
import com.techchallenge.os.application.gateway.ports.output.models.ClienteResumo;
import com.techchallenge.os.application.gateway.ports.output.models.MROResumo;
import com.techchallenge.os.application.gateway.ports.output.models.ServicoResumo;
import com.techchallenge.os.application.gateway.ports.output.models.VeiculoResumo;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.exceptions.ValidacaoClienteException;
import com.techchallenge.os.domain.exceptions.ValidacaoOrdemServicoException;
import com.techchallenge.os.domain.exceptions.ValidacaoVeiculoException;
import com.techchallenge.os.domain.models.ItemServico;
import com.techchallenge.os.domain.models.OrdemServico;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class CriarOrdemServicoUseCase implements CriarOrdemServicoInpuit {

    private final OrdemServicoGateway ordemServicoGateway;
    private final ClienteGateway clienteGateway;
    private final VeiculoGateway veiculoGateway;
    private final ServicoGateway servicoGateway;
    private final MroGateway mroGateway;
    private final OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    @Override
    public OrdemServicoResponse execute(CriarOrdemServicoCommand command) {
        log.info("Iniciando criação de ordem de serviço: cpfOuCnpj={}, placa={}",
                command.getCpfOuCnpj(), command.getPlaca());

        // 1. Resolver cliente por CPF ou CNPJ
        ClienteResumo cliente = resolverCliente(command.getCpfOuCnpj());

        // 2. Resolver veículo por placa
        VeiculoResumo veiculo = veiculoGateway.findByPlaca(command.getPlaca())
                .orElseThrow(() -> new ValidacaoVeiculoException(
                        "Veículo não encontrado com placa: " + command.getPlaca()));

        // 3. Criar a OS
        OrdemServico ordemServico = OrdemServico.criar(cliente.getId(), veiculo.getId());

        // 4. Adicionar serviços (por código)
        for (String codigoServico : command.getCodigosServico()) {
            ServicoResumo servicoDto = servicoGateway.buscarPorCodigo(codigoServico)
                    .orElseThrow(() -> new ValidacaoOrdemServicoException(
                            "Serviço não encontrado com código: " + codigoServico));

            if (Boolean.FALSE.equals(servicoDto.getAtivo())) {
                throw new ValidacaoOrdemServicoException(
                        "Serviço inativo: " + codigoServico);
            }

            ItemServico itemServico = ItemServico.criarComDados(
                    servicoDto.getId(),
                    servicoDto.getNome(),
                    servicoDto.getDescricao(),
                    servicoDto.getPreco());

            ordemServico.adicionarItemServico(itemServico);
        }

        // 5. Distribuir MROs nos itens de serviço (todos os MROs vão para o primeiro item)
        if (!command.getItensMRO().isEmpty()) {
            ItemServico primeiroItem = ordemServico.getItensServico().get(0);

            for (CriarOrdemServicoCommand.ItemMROCommand mroCmd : command.getItensMRO()) {
                MROResumo mroDto = mroGateway.buscarPorCodigo(mroCmd.getCodigoMro())
                        .orElseThrow(() -> new ValidacaoOrdemServicoException(
                                "MRO não encontrado com código: " + mroCmd.getCodigoMro()));

                if (Boolean.FALSE.equals(mroDto.getAtivo())) {
                    throw new ValidacaoOrdemServicoException(
                            "MRO inativo: " + mroCmd.getCodigoMro());
                }

                primeiroItem.adicionarMRO(
                        mroDto.getId(),
                        mroDto.getNome(),
                        mroDto.getDescricao(),
                        mroDto.getPrecoUnitario(),
                        mroCmd.getQuantidade());
            }
        }

        // 6. Persistir e retornar com orçamento calculado
        OrdemServico savedOrdemServico = ordemServicoGateway.save(ordemServico);

        log.info("Ordem de serviço criada com sucesso: ID={}, Status={}, ValorTotal={}",
                savedOrdemServico.getId(),
                savedOrdemServico.getStatus(),
                savedOrdemServico.calcularValorTotal());

        return ordemServicoResponseAssembler.montar(savedOrdemServico);
    }

    private ClienteResumo resolverCliente(String cpfOuCnpj) {
        String documento = cpfOuCnpj.replaceAll("\\D", "");

        if (documento.length() == 11) {
            return clienteGateway.findByCPF(documento)
                    .orElseThrow(() -> new ValidacaoClienteException(
                            "Cliente não encontrado com CPF: " + cpfOuCnpj));
        } else if (documento.length() == 14) {
            return clienteGateway.findByCNPJ(documento)
                    .orElseThrow(() -> new ValidacaoClienteException(
                            "Cliente não encontrado com CNPJ: " + cpfOuCnpj));
        } else {
            throw new ValidacaoOrdemServicoException(
                    "Identificação inválida. Informe um CPF (11 dígitos) ou CNPJ (14 dígitos).");
        }
    }
}
