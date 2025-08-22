package br.com.diego.hackathon.simulador.service;

import br.com.diego.hackathon.simulador.dto.*;
import br.com.diego.hackathon.simulador.exception.ProdutoNaoEncontradoException;
import br.com.diego.hackathon.simulador.model.Produto;
import br.com.diego.hackathon.simulador.model.Simulacao;
import br.com.diego.hackathon.simulador.repository.ProdutoRepository;
import br.com.diego.hackathon.simulador.repository.SimulacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela lógica de negócio das simulações de crédito.
 * 
 * Esta classe implementa os principais algoritmos financeiros:
 * - Sistema de Amortização Constante (SAC)
 * - Tabela Price (Sistema Francês)
 * - Validação de produtos compatíveis
 * - Persistência de simulações
 * - Consultas de histórico e telemetria
 * 
 * Todos os cálculos utilizam BigDecimal para garantir precisão financeira.
 * 
 * @author Diego
 * @version 1.0
 * @since 2023
 */
@Service
public class SimulacaoService {

    private final ProdutoRepository produtoRepository;
    private final SimulacaoRepository simulacaoRepository;

    /**
     * Construtor para injeção de dependências dos repositórios.
     * 
     * @param produtoRepository Repositório para acesso aos dados de produtos
     * @param simulacaoRepository Repositório para persistência das simulações
     */
    @Autowired
    public SimulacaoService(ProdutoRepository produtoRepository, SimulacaoRepository simulacaoRepository) {
        this.produtoRepository = produtoRepository;
        this.simulacaoRepository = simulacaoRepository;
    }

    /**
     * Método principal que orquestra todo o processo de simulação de crédito.
     * 
     * Este método executa o fluxo completo:
     * 1. Busca produtos disponíveis no banco de dados
     * 2. Valida compatibilidade com os parâmetros solicitados
     * 3. Calcula simulações usando SAC e Tabela Price
     * 4. Persiste a simulação no banco de dados
     * 5. Retorna o resultado formatado
     * 
     * @param request Dados da simulação (valor desejado e prazo)
     * @return SimulacaoResponse contendo os resultados dos dois sistemas de amortização
     * @throws ProdutoNaoEncontradoException quando nenhum produto é compatível
     */
    public SimulacaoResponse simular(SimulacaoRequest request) {
        // 1. Busca todos os produtos do banco de dados (SQL Server)
        List<Produto> produtos = produtoRepository.findAll();

        // 2. Valida e filtra para encontrar um produto compatível
        Produto produtoCompativel = encontrarProdutoCompativel(request, produtos)
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Nenhum produto compatível encontrado para os parâmetros informados."));

        // 3. Realiza os cálculos para os sistemas de amortização
        SimulacaoResult resultadoSac = calcularSac(request, produtoCompativel);
        SimulacaoResult resultadoPrice = calcularPrice(request, produtoCompativel);

        // 4. Salva a simulação no banco de dados
        salvarSimulacao(request, produtoCompativel, resultadoSac, resultadoPrice);

        // 5. Monta o objeto de resposta final
        return new SimulacaoResponse(produtoCompativel.getNome(), List.of(resultadoSac, resultadoPrice));
    }

    /**
     * Encontra um produto compatível com os parâmetros da simulação.
     * 
     * Valida se o valor desejado está dentro dos limites mínimo e máximo
     * do produto e se o prazo solicitado é aceito pelo produto.
     * 
     * @param request Dados da simulação solicitada
     * @param produtos Lista de produtos disponíveis
     * @return Optional contendo o primeiro produto compatível encontrado
     */
    private Optional<Produto> encontrarProdutoCompativel(SimulacaoRequest request, List<Produto> produtos) {
        return produtos.stream()
                .filter(produto -> request.valorDesejado().compareTo(produto.getValorMinimo()) >= 0 &&
                                   request.valorDesejado().compareTo(produto.getValorMaximo()) <= 0)
                .filter(produto -> request.prazo() >= produto.getPrazoMinimo() &&
                                   request.prazo() <= produto.getPrazoMaximo())
                .findFirst();
    }

    /**
     * Calcula a simulação usando o Sistema de Amortização Constante (SAC).
     * 
     * No SAC:
     * - A amortização é constante (valor principal ÷ número de parcelas)
     * - Os juros diminuem a cada parcela (incidem sobre o saldo devedor)
     * - A prestação diminui progressivamente
     * 
     * Fórmulas utilizadas:
     * - Amortização = Valor Principal ÷ Número de Parcelas
     * - Juros = Saldo Devedor × Taxa de Juros
     * - Prestação = Amortização + Juros
     * 
     * @param request Dados da simulação
     * @param produto Produto selecionado com suas configurações
     * @return SimulacaoResult contendo todas as parcelas calculadas
     */
    private SimulacaoResult calcularSac(SimulacaoRequest request, Produto produto) {
        List<Parcela> parcelas = new ArrayList<>();
        BigDecimal saldoDevedor = request.valorDesejado();
        BigDecimal taxaJurosMensal = produto.getTaxaJuros();
        int prazo = request.prazo();

        // No SAC, a amortização é constante (valor principal dividido pelo prazo)
        BigDecimal amortizacao = saldoDevedor.divide(BigDecimal.valueOf(prazo), 2, RoundingMode.HALF_UP);

        // Calcula cada parcela individualmente
        for (int i = 1; i <= prazo; i++) {
            // Juros incidem sobre o saldo devedor atual
            BigDecimal juros = saldoDevedor.multiply(taxaJurosMensal).setScale(2, RoundingMode.HALF_UP);
            // Prestação = amortização fixa + juros variáveis
            BigDecimal prestacao = amortizacao.add(juros);
            // Reduz o saldo devedor pela amortização
            saldoDevedor = saldoDevedor.subtract(amortizacao);

            parcelas.add(new Parcela(i, amortizacao, juros, prestacao));
        }

        return new SimulacaoResult("SAC", parcelas);
    }

    /**
     * Calcula a simulação usando a Tabela Price (Sistema Francês).
     * 
     * Na Tabela Price:
     * - A prestação é constante durante todo o financiamento
     * - A amortização cresce progressivamente
     * - Os juros diminuem progressivamente
     * 
     * Fórmula da prestação fixa (PMT):
     * PMT = PV × [i × (1+i)^n] / [(1+i)^n - 1]
     * 
     * Onde:
     * - PV = Valor Presente (valor financiado)
     * - i = Taxa de juros por período
     * - n = Número de períodos
     * 
     * @param request Dados da simulação
     * @param produto Produto selecionado com suas configurações
     * @return SimulacaoResult contendo todas as parcelas calculadas
     */
    private SimulacaoResult calcularPrice(SimulacaoRequest request, Produto produto) {
        List<Parcela> parcelas = new ArrayList<>();
        BigDecimal saldoDevedor = request.valorDesejado();
        BigDecimal taxaJurosMensal = produto.getTaxaJuros();
        int prazo = request.prazo();

        // Fórmula da Tabela Price para calcular a prestação fixa
        // PMT = PV * [i * (1+i)^n] / [(1+i)^n - 1]
        BigDecimal umMaisI = BigDecimal.ONE.add(taxaJurosMensal);
        BigDecimal umMaisIelevadoN = umMaisI.pow(prazo);
        BigDecimal pmt = saldoDevedor
                .multiply(taxaJurosMensal.multiply(umMaisIelevadoN))
                .divide(umMaisIelevadoN.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);

        // Calcula cada parcela com prestação fixa
        for (int i = 1; i <= prazo; i++) {
            // Juros incidem sobre o saldo devedor atual
            BigDecimal juros = saldoDevedor.multiply(taxaJurosMensal).setScale(2, RoundingMode.HALF_UP);
            // Amortização = prestação fixa - juros variáveis
            BigDecimal amortizacao = pmt.subtract(juros);
            // Reduz o saldo devedor pela amortização
            saldoDevedor = saldoDevedor.subtract(amortizacao);

            parcelas.add(new Parcela(i, amortizacao, juros, pmt));
        }

        return new SimulacaoResult("PRICE", parcelas);
    }

    /**
     * Persiste a simulação realizada no banco de dados.
     * 
     * Este método calcula os totais da simulação baseado no resultado SAC
     * e salva todas as informações relevantes para consultas futuras.
     * 
     * Dados salvos:
     * - Parâmetros da simulação (valor, prazo)
     * - Produto utilizado
     * - Totais calculados (parcelas, juros, amortização)
     * - Data e hora da simulação
     * 
     * @param request Dados originais da simulação
     * @param produto Produto selecionado
     * @param resultadoSac Resultado do cálculo SAC (usado para totais)
     * @param resultadoPrice Resultado do cálculo Price (para referência)
     */
    private void salvarSimulacao(SimulacaoRequest request, Produto produto, SimulacaoResult resultadoSac, SimulacaoResult resultadoPrice) {
        // Calcula valores totais baseados no resultado SAC
        BigDecimal valorTotalParcelas = resultadoSac.parcelas().stream()
                .map(Parcela::valorPrestacao)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorTotalJuros = resultadoSac.parcelas().stream()
                .map(Parcela::valorJuros)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorTotalAmortizacao = resultadoSac.parcelas().stream()
                .map(Parcela::valorAmortizacao)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Cria e popula a entidade Simulacao
        Simulacao simulacao = new Simulacao();
        simulacao.setIdSimulacao(UUID.randomUUID().toString());
        simulacao.setValorDesejado(request.valorDesejado());
        simulacao.setPrazo(request.prazo());
        simulacao.setValorTotalParcelas(valorTotalParcelas);
        simulacao.setDataSimulacao(LocalDateTime.now());
        simulacao.setProduto(produto);
        simulacao.setCodigoProduto(produto.getId());
        simulacao.setDescricaoProduto(produto.getNome());
        simulacao.setTaxaJuros(produto.getTaxaJuros());
        simulacao.setValorAmortizacao(valorTotalAmortizacao);
        simulacao.setValorTotalJuros(valorTotalJuros);
        simulacao.setValorTotalGeral(valorTotalParcelas);

        // Persiste no banco de dados
        simulacaoRepository.save(simulacao);
    }

    /**
     * Busca simulações filtradas por código do produto e data específica.
     * 
     * Este método consulta o histórico de simulações realizadas para um
     * produto específico em uma determinada data, retornando informações
     * detalhadas de cada simulação encontrada.
     * 
     * A consulta considera o dia inteiro (00:00:00 até 23:59:59) da data
     * informada para capturar todas as simulações do período.
     * 
     * @param codigoProduto Código identificador do produto
     * @param dataReferencia Data para filtrar as simulações
     * @return SimulacoesPorProdutoResponse contendo lista de simulações encontradas
     */
    public SimulacoesPorProdutoResponse buscarSimulacoesPorProdutoEData(Integer codigoProduto, LocalDate dataReferencia) {
        // Define o intervalo de tempo para busca (dia completo)
        LocalDateTime dataInicio = dataReferencia.atStartOfDay();
        LocalDateTime dataFim = dataReferencia.atTime(23, 59, 59);
        
        // Busca simulações no repositório usando query customizada
        List<Simulacao> simulacoes = simulacaoRepository.findByCodigoProdutoAndData(codigoProduto, dataInicio, dataFim);
        
        // Converte entidades para DTOs de resposta
        List<SimulacoesPorProdutoResponse.SimulacaoDetalhada> simulacoesDetalhadas = simulacoes.stream()
                .map(s -> new SimulacoesPorProdutoResponse.SimulacaoDetalhada(
                        s.getCodigoProduto(),
                        s.getDescricaoProduto(),
                        s.getTaxaJuros(),
                        s.getValorAmortizacao(),
                        s.getValorTotalJuros(),
                        s.getValorTotalGeral()
                ))
                .collect(Collectors.toList());
        
        return new SimulacoesPorProdutoResponse(
                dataReferencia.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                simulacoesDetalhadas
        );
    }

    /**
     * Obtém dados de telemetria do sistema para uma data específica.
     * 
     * Este método retorna informações sobre o estado e performance do sistema,
     * incluindo métricas de uso de recursos computacionais. Útil para
     * monitoramento, diagnóstico e análise de performance.
     * 
     * Atualmente retorna dados simulados, mas pode ser expandido para
     * integrar com ferramentas de monitoramento reais.
     * 
     * @param dataReferencia Data de referência para os dados de telemetria
     * @return TelemetriaResponse contendo métricas do sistema
     */
    public TelemetriaResponse obterTelemetria(LocalDate dataReferencia) {
        // Dados simulados de telemetria baseados na estrutura das imagens
        // Em produção, estes dados viriam de ferramentas de monitoramento
        List<TelemetriaResponse.DadosTelemetria> dadosTelemetria = List.of(
                new TelemetriaResponse.DadosTelemetria(
                        "simulacao",    // Nome do serviço
                        135,            // Memória livre (MB)
                        150,            // Memória total (MB)
                        23,             // Percentual de CPU
                        850,            // Espaço livre em disco (GB)
                        0.98            // Uptime do sistema
                )
        );
        
        return new TelemetriaResponse(
                dataReferencia.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                dadosTelemetria
        );
    }
}
