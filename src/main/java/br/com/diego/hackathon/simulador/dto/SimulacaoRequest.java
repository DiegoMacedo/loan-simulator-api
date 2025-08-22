package br.com.diego.hackathon.simulador.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) para requisições de simulação de crédito.
 * 
 * Este record representa os dados de entrada necessários para criar uma
 * nova simulação de empréstimo. Utiliza Bean Validation para garantir
 * que os dados recebidos estejam dentro dos parâmetros válidos.
 * 
 * Validações aplicadas:
 * - valorDesejado: Deve ser não nulo e maior que R$ 0,01
 * - prazo: Deve ser não nulo e pelo menos 1 mês
 * 
 * Exemplo de uso em JSON:
 * {
 *   "valorDesejado": 10000.00,
 *   "prazo": 12
 * }
 * 
 * @param valorDesejado Valor monetário que o cliente deseja solicitar
 * @param prazo Número de meses para pagamento do empréstimo
 */
public record SimulacaoRequest(
        /**
         * Valor monetário desejado para o empréstimo.
         * Deve ser maior que zero e será usado para encontrar produtos compatíveis.
         */
        @NotNull
        @DecimalMin(value = "0.01", message = "Valor desejado deve ser maior que zero")
        BigDecimal valorDesejado,
        
        /**
         * Prazo desejado para pagamento em meses.
         * Deve ser pelo menos 1 mês e será usado nos cálculos de amortização.
         */
        @NotNull
        @Min(value = 1, message = "Prazo deve ser pelo menos 1 mês")
        Integer prazo
) {
}