package br.com.diego.hackathon.simulador.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public record SimulacoesPorProdutoResponse(
        @JsonProperty("dataReferencia")
        String dataReferencia,
        
        @JsonProperty("simulacoes")
        List<SimulacaoDetalhada> simulacoes
) {
    
    public record SimulacaoDetalhada(
            @JsonProperty("codigoProduto")
            Integer codigoProduto,
            
            @JsonProperty("descricaoProduto")
            String descricaoProduto,
            
            @JsonProperty("taxaJuros")
            BigDecimal taxaJuros,
            
            @JsonProperty("valorAmortizacao")
            BigDecimal valorAmortizacao,
            
            @JsonProperty("valorTotalJuros")
            BigDecimal valorTotalJuros,
            
            @JsonProperty("valorTotalGeral")
            BigDecimal valorTotalGeral
    ) {}
}