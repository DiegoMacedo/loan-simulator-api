package br.com.diego.hackathon.simulador.dto;

import java.util.List;

/**
 * DTO (Data Transfer Object) que representa o resultado de um cálculo de amortização.
 * 
 * Este record contém os dados calculados para um sistema específico de amortização
 * (SAC ou Tabela Price), incluindo todas as parcelas detalhadas do empréstimo.
 * 
 * Tipos de amortização suportados:
 * - "SAC": Sistema de Amortização Constante (parcelas decrescentes)
 * - "PRICE": Tabela Price (parcelas fixas)
 * 
 * Cada resultado contém uma lista completa de parcelas com valores detalhados
 * de amortização, juros e prestação para cada mês do empréstimo.
 * 
 * Exemplo de estrutura JSON:
 * {
 *   "tipo": "SAC",
 *   "parcelas": [
 *     {
 *       "numero": 1,
 *       "valorAmortizacao": 833.33,
 *       "valorJuros": 150.00,
 *       "valorPrestacao": 983.33
 *     },
 *     ...
 *   ]
 * }
 * 
 * @param tipo Tipo do sistema de amortização ("SAC" ou "PRICE")
 * @param parcelas Lista detalhada de todas as parcelas do empréstimo
 */
public record SimulacaoResult(
        /**
         * Tipo do sistema de amortização utilizado no cálculo.
         * Valores possíveis: "SAC" (Sistema de Amortização Constante) ou "PRICE" (Tabela Price).
         */
        String tipo, // "SAC" ou "PRICE"
        
        /**
         * Lista completa das parcelas calculadas para este sistema de amortização.
         * Cada parcela contém valores detalhados de amortização, juros e prestação.
         */
        List<Parcela> parcelas
) {
}
