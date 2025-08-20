package br.com.diego.hackathon.simulador.dto;

import java.util.List;

public record SimulacaoResult(
        String tipo, // "SAC" ou "PRICE"
        List<Parcela> parcelas
) {
}
