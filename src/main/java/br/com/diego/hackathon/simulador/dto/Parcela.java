package br.com.diego.hackathon.simulador.dto;

import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) que representa uma parcela individual de um empréstimo.
 * 
 * Este record contém todos os valores financeiros detalhados de uma parcela específica,
 * incluindo a decomposição entre amortização do capital, juros e valor total da prestação.
 * 
 * Utiliza BigDecimal para garantir precisão nos cálculos financeiros, evitando
 * problemas de arredondamento que poderiam ocorrer com tipos primitivos como double.
 * 
 * Os valores são calculados de acordo com o sistema de amortização escolhido:
 * - SAC: Amortização constante, juros decrescentes, prestação decrescente
 * - Price: Prestação constante, amortização crescente, juros decrescentes
 * 
 * Exemplo de estrutura JSON:
 * {
 *   "numero": 1,
 *   "valorAmortizacao": 833.33,
 *   "valorJuros": 150.00,
 *   "valorPrestacao": 983.33
 * }
 * 
 * @param numero Número sequencial da parcela (1, 2, 3, ...)
 * @param valorAmortizacao Valor da amortização do capital nesta parcela
 * @param valorJuros Valor dos juros incidentes nesta parcela
 * @param valorPrestacao Valor total da prestação (amortização + juros)
 */
public record Parcela(
        /**
         * Número sequencial da parcela.
         * Inicia em 1 e vai até o prazo total do empréstimo.
         */
        int numero,
        
        /**
         * Valor da amortização do capital principal nesta parcela.
         * No SAC este valor é constante, na Tabela Price é crescente.
         */
        BigDecimal valorAmortizacao,
        
        /**
         * Valor dos juros incidentes sobre o saldo devedor nesta parcela.
         * Em ambos os sistemas (SAC e Price) este valor é decrescente.
         */
        BigDecimal valorJuros,
        
        /**
         * Valor total da prestação (amortização + juros).
         * No SAC este valor é decrescente, na Tabela Price é constante.
         */
        BigDecimal valorPrestacao
) {
}
