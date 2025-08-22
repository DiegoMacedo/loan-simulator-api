package br.com.diego.hackathon.simulador.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO (Data Transfer Object) para resposta de consulta de simulações por produto.
 * 
 * Este record representa o resultado de uma busca de simulações filtradas por
 * código de produto e período de data, fornecendo um resumo consolidado das
 * simulações realizadas.
 * 
 * A resposta inclui:
 * - Data de referência da consulta
 * - Lista de simulações detalhadas encontradas
 * 
 * Utiliza anotações @JsonProperty para garantir compatibilidade com
 * sistemas externos que possam esperar nomes específicos de campos.
 * 
 * Exemplo de estrutura JSON retornada:
 * {
 *   "dataReferencia": "2024-01-15",
 *   "simulacoes": [
 *     {
 *       "codigoProduto": 1,
 *       "descricaoProduto": "Crédito Pessoal Premium",
 *       "taxaJuros": 1.5,
 *       "valorAmortizacao": 10000.00,
 *       "valorTotalJuros": 1500.00,
 *       "valorTotalGeral": 11500.00
 *     }
 *   ]
 * }
 * 
 * @param dataReferencia Data de referência para a consulta realizada
 * @param simulacoes Lista de simulações encontradas com seus detalhes
 */
public record SimulacoesPorProdutoResponse(
        /**
         * Data de referência para a consulta de simulações.
         * Formato: yyyy-MM-dd
         */
        @JsonProperty("dataReferencia")
        String dataReferencia,
        
        /**
         * Lista contendo as simulações encontradas para o produto e período especificados.
         * Cada item representa uma simulação com seus valores consolidados.
         */
        @JsonProperty("simulacoes")
        List<SimulacaoDetalhada> simulacoes
) {
    
    /**
     * Record interno que representa os detalhes consolidados de uma simulação.
     * 
     * Contém informações resumidas sobre uma simulação específica, incluindo
     * dados do produto utilizado e valores financeiros calculados.
     * 
     * Os valores são apresentados de forma consolidada, representando os totais
     * calculados para a simulação (independente do sistema de amortização).
     * 
     * @param codigoProduto Código identificador do produto financeiro
     * @param descricaoProduto Nome/descrição do produto financeiro
     * @param taxaJuros Taxa de juros aplicada na simulação
     * @param valorAmortizacao Valor total de amortização do capital
     * @param valorTotalJuros Valor total de juros a serem pagos
     * @param valorTotalGeral Valor total geral do empréstimo (capital + juros)
     */
    public record SimulacaoDetalhada(
            /**
             * Código identificador único do produto financeiro utilizado na simulação.
             */
            @JsonProperty("codigoProduto")
            Integer codigoProduto,
            
            /**
             * Nome ou descrição do produto financeiro.
             * Exemplo: "Crédito Pessoal Premium", "Financiamento Imobiliário"
             */
            @JsonProperty("descricaoProduto")
            String descricaoProduto,
            
            /**
             * Taxa de juros mensal aplicada na simulação.
             * Valor percentual (ex: 1.5 representa 1,5% ao mês).
             */
            @JsonProperty("taxaJuros")
            BigDecimal taxaJuros,
            
            /**
             * Valor total de amortização do capital principal.
             * Representa o valor original solicitado no empréstimo.
             */
            @JsonProperty("valorAmortizacao")
            BigDecimal valorAmortizacao,
            
            /**
             * Valor total de juros a serem pagos durante todo o período do empréstimo.
             * Soma de todos os juros de todas as parcelas.
             */
            @JsonProperty("valorTotalJuros")
            BigDecimal valorTotalJuros,
            
            /**
             * Valor total geral do empréstimo (capital + juros).
             * Representa o valor total que será pago pelo cliente.
             */
            @JsonProperty("valorTotalGeral")
            BigDecimal valorTotalGeral
    ) {}
}