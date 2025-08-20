package br.com.diego.hackathon.simulador.dto;

import java.util.List;

public record SimulacaoResponse(
        String nomeProduto,
        List<SimulacaoResult> resultados
) {
}
