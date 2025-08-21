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

@Service
public class SimulacaoService {

    private final ProdutoRepository produtoRepository;
    private final SimulacaoRepository simulacaoRepository;

    @Autowired
    public SimulacaoService(ProdutoRepository produtoRepository, SimulacaoRepository simulacaoRepository) {
        this.produtoRepository = produtoRepository;
        this.simulacaoRepository = simulacaoRepository;
    }

    // Método principal que orquestra a simulação
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

    private Optional<Produto> encontrarProdutoCompativel(SimulacaoRequest request, List<Produto> produtos) {
        return produtos.stream()
                .filter(produto -> request.valorDesejado().compareTo(produto.getValorMinimo()) >= 0 &&
                                   request.valorDesejado().compareTo(produto.getValorMaximo()) <= 0)
                .filter(produto -> request.prazo() >= produto.getPrazoMinimo() &&
                                   request.prazo() <= produto.getPrazoMaximo())
                .findFirst();
    }

    private SimulacaoResult calcularSac(SimulacaoRequest request, Produto produto) {
        List<Parcela> parcelas = new ArrayList<>();
        BigDecimal saldoDevedor = request.valorDesejado();
        BigDecimal taxaJurosMensal = produto.getTaxaJuros();
        int prazo = request.prazo();

        // No SAC, a amortização é constante
        BigDecimal amortizacao = saldoDevedor.divide(BigDecimal.valueOf(prazo), 2, RoundingMode.HALF_UP);

        for (int i = 1; i <= prazo; i++) {
            BigDecimal juros = saldoDevedor.multiply(taxaJurosMensal).setScale(2, RoundingMode.HALF_UP);
            BigDecimal prestacao = amortizacao.add(juros);
            saldoDevedor = saldoDevedor.subtract(amortizacao);

            parcelas.add(new Parcela(i, amortizacao, juros, prestacao));
        }

        return new SimulacaoResult("SAC", parcelas);
    }

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

        for (int i = 1; i <= prazo; i++) {
            BigDecimal juros = saldoDevedor.multiply(taxaJurosMensal).setScale(2, RoundingMode.HALF_UP);
            BigDecimal amortizacao = pmt.subtract(juros);
            saldoDevedor = saldoDevedor.subtract(amortizacao);

            parcelas.add(new Parcela(i, amortizacao, juros, pmt));
        }

        return new SimulacaoResult("PRICE", parcelas);
    }

    private void salvarSimulacao(SimulacaoRequest request, Produto produto, SimulacaoResult resultadoSac, SimulacaoResult resultadoPrice) {
        // Calcula valores totais
        BigDecimal valorTotalParcelas = resultadoSac.parcelas().stream()
                .map(Parcela::valorPrestacao)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorTotalJuros = resultadoSac.parcelas().stream()
                .map(Parcela::valorJuros)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorTotalAmortizacao = resultadoSac.parcelas().stream()
                .map(Parcela::valorAmortizacao)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

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

        simulacaoRepository.save(simulacao);
    }

    public SimulacoesPorProdutoResponse buscarSimulacoesPorProdutoEData(Integer codigoProduto, LocalDate dataReferencia) {
        LocalDateTime dataInicio = dataReferencia.atStartOfDay();
        LocalDateTime dataFim = dataReferencia.atTime(23, 59, 59);
        
        List<Simulacao> simulacoes = simulacaoRepository.findByCodigoProdutoAndData(codigoProduto, dataInicio, dataFim);
        
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

    public TelemetriaResponse obterTelemetria(LocalDate dataReferencia) {
        // Dados simulados de telemetria baseados na estrutura das imagens
        List<TelemetriaResponse.DadosTelemetria> dadosTelemetria = List.of(
                new TelemetriaResponse.DadosTelemetria(
                        "simulacao",
                        135,
                        150,
                        23,
                        850,
                        0.98
                )
        );
        
        return new TelemetriaResponse(
                dataReferencia.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                dadosTelemetria
        );
    }
}
