package br.com.diego.hackathon.simulador.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record SimulacaoRequest(
        @NotNull
        @DecimalMin(value = "0.01", message = "Valor desejado deve ser maior que zero")
        BigDecimal valorDesejado,
        
        @NotNull
        @Min(value = 1, message = "Prazo deve ser pelo menos 1 mês")
        Integer prazo
) {
}