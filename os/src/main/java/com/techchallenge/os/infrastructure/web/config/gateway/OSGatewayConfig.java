package com.techchallenge.os.infrastructure.web.config.gateway;

import com.techchallenge.os.application.gateway.ports.input.*;
import com.techchallenge.os.application.gateway.ports.output.*;
import com.techchallenge.os.application.usecases.*;
import com.techchallenge.os.application.usecases.assemblers.OrdemServicoResponseAssembler;
import com.techchallenge.os.infrastructure.notifications.NotificacaoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OSGatewayConfig {

    @Bean
    public CriarOrdemServicoInpuit criarOrdemServicoInput(OrdemServicoGateway ordemServicoGateway,
                                                          ClienteGateway clienteGateway,
                                                          VeiculoGateway veiculoGateway,
                                                          ServicoGateway servicoGateway,
                                                          MroGateway mroGateway,
                                                          OrdemServicoResponseAssembler ordemServicoResponseAssembler
                                                          ) {
        return new CriarOrdemServicoUseCase(ordemServicoGateway, clienteGateway, veiculoGateway, servicoGateway, mroGateway, ordemServicoResponseAssembler);
    }

    @Bean
    public ListarOrdensServicoInput listarOrdensServicoInput(OrdemServicoGateway ordemServicoGateway,
                                                             ServicoGateway servicoGateway,
                                                             MroGateway mroGateway,
                                                             OrdemServicoResponseAssembler ordemServicoResponseAssembl) {
        return new ListarOrdensServicoUseCase(ordemServicoGateway, servicoGateway, mroGateway, ordemServicoResponseAssembl);
    }

    @Bean
    public BuscarOrdemServicoInput buscarOrdemServicoInput(OrdemServicoGateway ordemServicoGateway,
                                                           OrdemServicoResponseAssembler ordemServicoResponseAssembler) {
        return new BuscarOrdemServicoUseCase(ordemServicoGateway, ordemServicoResponseAssembler);
    }

    @Bean
    public AdicionarServicoOrdemInput adicionarServicoOrdemInput(OrdemServicoGateway ordemServicoGateway,
                                                                 ServicoGateway servicoGateway,
                                                                 OrdemServicoResponseAssembler ordemServicoResponseAssembler) {
        return new AdicionarServicoOrdemUseCase(ordemServicoGateway, servicoGateway, ordemServicoResponseAssembler);
    }

    @Bean
    public RemoverServicoOrdemInput removerServicoOrdemInput(OrdemServicoGateway ordemServicoGateway,
                                                             OrdemServicoResponseAssembler ordemServicoResponseAssembler) {
        return new RemoverServicoOrdemUseCase(ordemServicoGateway, ordemServicoResponseAssembler);
    }

    @Bean
    public AdicionarMROServicoInput adicionarMROServicoInput(OrdemServicoGateway ordemServicoGateway,
                                                             MroGateway mroGateway,
                                                             OrdemServicoResponseAssembler ordemServicoResponseAssembler) {
        return new AdicionarMROServicoUseCase(ordemServicoGateway, mroGateway, ordemServicoResponseAssembler);
    }

    @Bean
    public RemoverMROServicoInput removerMROServicoInput(OrdemServicoGateway ordemServicoGateway,
                                                         OrdemServicoResponseAssembler ordemServicoResponseAssembler) {
        return new RemoverMROServicoUseCase(ordemServicoGateway, ordemServicoResponseAssembler);
    }

    @Bean
    public AtualizarQuantidadeMROInput atualizarQuantidadeMROInput(OrdemServicoGateway ordemServicoGateway,
                                                                   OrdemServicoResponseAssembler ordemServicoResponseAssembler) {
        return new AtualizarQuantidadeMROUseCase(ordemServicoGateway, ordemServicoResponseAssembler);
    }

    @Bean
    public EnviarParaDiagnosticoInput enviarParaDiagnosticoInput(OrdemServicoGateway ordemServicoGateway,
                                                                 OrdemServicoResponseAssembler ordemServicoResponseAssembler) {
        return new EnviarParaDiagnosticoUseCase(ordemServicoGateway, ordemServicoResponseAssembler);
    }

    @Bean
    public EnviarOrcamentoAoClienteInput enviarOrcamentoAoClienteInput(OrdemServicoGateway ordemServicoGateway,
                                                                       OrdemServicoResponseAssembler ordemServicoResponseAssembler,
                                                                       ClienteGateway clienteGateway,
                                                                       OsEventPublisher osEventPublisher) {
        return new EnviarOrcamentoAoClienteUseCase(
                ordemServicoGateway,
                ordemServicoResponseAssembler,
                clienteGateway,
                osEventPublisher
        );
    }

    @Bean
    public NotificarClienteOrcamentoComPixUseCase notificarClienteOrcamentoComPixUseCase(
            ClienteGateway clienteGateway,
            NotificacaoOrcamentoComPixGateway notificacaoGateway) {
        return new NotificarClienteOrcamentoComPixUseCase(clienteGateway, notificacaoGateway);
    }

    @Bean
    public AtualizarObservacoesServicoInput atualizarObservacoesServicoInput(OrdemServicoGateway ordemServicoGateway,
                                                                             OrdemServicoResponseAssembler ordemServicoResponseAssembler) {
        return new AtualizarObservacoesServicoUseCase(ordemServicoGateway, ordemServicoResponseAssembler);
    }

    @Bean
    public IniciarServicoInput iniciarServicoInput(OrdemServicoGateway ordemServicoGateway,
                                                   MroGateway mroGateway,
                                                   OrdemServicoResponseAssembler ordemServicoResponseAssembler) {
        return new IniciarServicoUseCase(ordemServicoGateway, mroGateway, ordemServicoResponseAssembler);
    }

    @Bean
    public ConcluirServicoInput concluirServicoInput(OrdemServicoGateway ordemServicoGateway,
                                                     OrdemServicoResponseAssembler ordemServicoResponseAssembler) {
        return new ConcluirServicoUseCase(ordemServicoGateway, ordemServicoResponseAssembler);
    }

    @Bean
    public CancelarServicoInput cancelarServicoInput(OrdemServicoGateway ordemServicoGateway,
                                                     OrdemServicoResponseAssembler ordemServicoResponseAssembler) {
        return new CancelarServicoUseCase(ordemServicoGateway, ordemServicoResponseAssembler);
    }

    @Bean
    public EntregarOrdemServicoInput entregarOrdemServicoInput(OrdemServicoGateway ordemServicoGateway,
                                                               OrdemServicoResponseAssembler ordemServicoResponseAssembler) {
        return new EntregarOrdemServicoUseCase(ordemServicoGateway, ordemServicoResponseAssembler);
    }

    @Bean
    public CalcularTempoMedioExecucaoInput calcularTempoMedioExecucaoInput(OrdemServicoGateway ordemServicoGateway) {
        return new CalcularTempoMedioExecucaoUseCase(ordemServicoGateway);
    }

    @Bean
    public AprovarOrcamentoInput aprovarOrcamentoInput(OrdemServicoGateway ordemServicoGateway,
                                                       OrdemServicoResponseAssembler ordemServicoResponseAssembler){
        return new AprovarOrcamentoUseCase(ordemServicoGateway, ordemServicoResponseAssembler);
    }

    @Bean
    public RecusarOrcamentoInput recusarOrcamentoInput(OrdemServicoGateway ordemServicoGateway,
                                                       OrdemServicoResponseAssembler ordemServicoResponseAssembler) {
        return new RecusarOrcamentoUseCase(ordemServicoGateway, ordemServicoResponseAssembler);
    }

    @Bean
    public ListarOrdensServicoPorClienteInput listarOrdensServicoPorClienteInput(OrdemServicoGateway ordemServicoGateway,
                                                                                 OrdemServicoResponseAssembler ordemServicoResponseAssembler){
        return new ListarOrdensServicoPorClienteUseCase(ordemServicoGateway, ordemServicoResponseAssembler);
    }


}
