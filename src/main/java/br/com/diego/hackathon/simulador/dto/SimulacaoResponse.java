package br.com.diego.hackathon.simulador.dto;

import java.util.List;

/**
 * DTO (Data Transfer Object) para resposta de simulação de crédito.
 * 
 * Este record representa o resultado completo de uma simulação de empréstimo,
 * contendo o produto financeiro encontrado e os cálculos realizados para
 * ambos os sistemas de amortização (SAC e Tabela Price).
 * 
 * A resposta inclui:
 * - Nome do produto financeiro compatível encontrado
 * - Lista com os resultados dos dois tipos de amortização
 * 
 * Exemplo de estrutura JSON retornada:
 * {
 *   "nomeProduto": "Crédito Pessoal Premium",
 *   "resultados": [
 *     {
 *       "tipo": "SAC",
 *       "parcelas": [...]
 *     },
 *     {
 *       "tipo": "PRICE",
 *       "parcelas": [...]
 *     }
 *   ]
 * }
 * 
 * @param nomeProduto Nome do produto financeiro utilizado na simulação
 * @param resultados Lista contendo os cálculos para SAC e Tabela Price
 */
public record SimulacaoResponse(
        /**
         * Nome do produto financeiro encontrado e utilizado na simulação.
         * Representa o produto que melhor se adequa aos parâmetros solicitados.
         */
        String nomeProduto,
        
        /**
         * Lista com os resultados dos cálculos de amortização.
         * Sempre contém dois elementos: um para SAC e outro para Tabela Price.
         */
        List<SimulacaoResult> resultados
) {
}
