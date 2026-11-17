package com.techchallenge.os.application.usecases;

import com.techchallenge.os.application.gateway.ports.input.ListarOrdensServicoInput;
import com.techchallenge.os.application.gateway.ports.output.MroGateway;
import com.techchallenge.os.application.gateway.ports.output.OrdemServicoGateway;
import com.techchallenge.os.application.gateway.ports.output.ServicoGateway;
import com.techchallenge.os.application.gateway.ports.output.models.MROResumo;
import com.techchallenge.os.application.gateway.ports.output.models.ServicoResumo;
import com.techchallenge.os.application.responses.ItemMROResponse;
import com.techchallenge.os.application.responses.ItemServicoResponse;
import com.techchallenge.os.application.responses.OrdemServicoResponse;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.domain.models.OrdemServico;
import com.techchallenge.os.domain.models.valueobjects.StatusOS;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class ListarOrdensServicoUseCase implements ListarOrdensServicoInput {
    
    private final OrdemServicoGateway ordemServicoGateway;
    private final ServicoGateway servicoGateway;
    private final MroGateway mroGateway;
    private final OrdemServicoResponseAssembler ordemServicoResponseAssembler;

    public Page<OrdemServicoResponse> execute(UUID clienteId, UUID veiculoId, StatusOS status,
                                              LocalDateTime dataInicio, LocalDateTime dataFim,
                                              Pageable pageable) {
        log.info("Listando ordens de serviço com filtros: clienteId={}, veiculoId={}, status={}, dataInicio={}, dataFim={}", 
                clienteId, veiculoId, status, dataInicio, dataFim);
        
        Page<OrdemServico> ordensServico = ordemServicoGateway.findByFilters(
                clienteId, veiculoId, status, dataInicio, dataFim, pageable);
        
        return ordensServico.map(this::enrichOrdemServicoResponse);
    }
    
    private OrdemServicoResponse enrichOrdemServicoResponse(OrdemServico ordemServico) {
        OrdemServicoResponse response = ordemServicoResponseAssembler.montar(ordemServico);
        
        // Buscar dados de serviços e MROs via ACL
        if (response.getItensServico() != null && !response.getItensServico().isEmpty()) {
            List<UUID> servicoIds = response.getItensServico().stream()
                    .map(ItemServicoResponse::getServicoId)
                    .distinct()
                    .toList();
            
            List<UUID> mroIds = response.getItensServico().stream()
                    .flatMap(item -> item.getMros() != null ? item.getMros().stream() : List.<ItemMROResponse>of().stream())
                    .map(ItemMROResponse::getMroId)
                    .distinct()
                    .toList();
            
            // Buscar serviços em lote
            Map<UUID, ServicoResumo> servicosMap = servicoIds.stream()
                    .collect(Collectors.toMap(
                            id -> id,
                            id -> servicoGateway.findById(id).orElse(null)
                    ));
            
            // Buscar MROs em lote
            Map<UUID, MROResumo> mrosMap = mroIds.stream()
                    .collect(Collectors.toMap(
                            id -> id,
                            id -> mroGateway.findById(id).orElse(null)
                    ));
            
            // Preencher dados nos itens de serviço
            response.getItensServico().forEach(item -> {
                ServicoResumo servico = servicosMap.get(item.getServicoId());
                if (servico != null) {
                    item.setServicoNome(servico.getNome());
                    item.setServicoDescricao(servico.getDescricao());
                }
                
                // Preencher dados nos MROs
                if (item.getMros() != null) {
                    item.getMros().forEach(mro -> {
                        MROResumo mroResponse = mrosMap.get(mro.getMroId());
                        if (mroResponse != null) {
                            mro.setMroNome(mroResponse.getNome());
                            mro.setMroDescricao(mroResponse.getDescricao());
                        }
                    });
                }
            });
        }
        
        return response;
    }
}
