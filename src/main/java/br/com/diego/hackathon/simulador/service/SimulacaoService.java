package br.com.diego.hackathon.simulador.service;

import br.com.diego.hackathon.simulador.dto.Parcela;
import br.com.diego.hackathon.simulador.dto.SimulacaoRequest;
import br.com.diego.hackathon.simulador.dto.SimulacaoResponse;
import br.com.diego.hackathon.simulador.dto.SimulacaoResult;
import br.com.diego.hackathon.simulador.exception.ProdutoNaoEncontradoException;
import br.com.diego.hackathon.simulador.model.Produto;
import br.com.diego.hackathon.simulador.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SimulacaoService {

    private final ProdutoRepository produtoRepository;

    @Autowired
    public SimulacaoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
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

        // 4. Monta o objeto de resposta final
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
}
