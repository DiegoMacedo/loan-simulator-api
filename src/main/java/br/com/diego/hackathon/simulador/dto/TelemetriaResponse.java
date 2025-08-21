package br.com.diego.hackathon.simulador.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TelemetriaResponse(
        @JsonProperty("dataReferencia")
        String dataReferencia,
        
        @JsonProperty("listaDados")
        List<DadosTelemetria> listaDados
) {
    
    public record DadosTelemetria(
            @JsonProperty("nomeApi")
            String nomeApi,
            
            @JsonProperty("qtdRequisicoes")
            Integer qtdRequisicoes,
            
            @JsonProperty("tempoMedio")
            Integer tempoMedio,
            
            @JsonProperty("tempoMinimo")
            Integer tempoMinimo,
            
            @JsonProperty("tempoMaximo")
            Integer tempoMaximo,
            
            @JsonProperty("percentualSucesso")
            Double percentualSucesso
    ) {}
}