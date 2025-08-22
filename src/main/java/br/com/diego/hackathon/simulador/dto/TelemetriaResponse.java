package br.com.diego.hackathon.simulador.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO (Data Transfer Object) para resposta de telemetria da API.
 * 
 * Este record representa dados de monitoramento e performance da API,
 * fornecendo métricas importantes sobre o uso e comportamento dos endpoints.
 * 
 * A telemetria inclui informações como:
 * - Data de referência dos dados coletados
 * - Lista de métricas por endpoint da API
 * 
 * Utiliza anotações @JsonProperty para garantir compatibilidade com
 * sistemas externos que possam esperar nomes específicos de campos.
 * 
 * Exemplo de estrutura JSON retornada:
 * {
 *   "dataReferencia": "2024-01-15",
 *   "listaDados": [
 *     {
 *       "nomeApi": "POST /api/simulacao",
 *       "qtdRequisicoes": 150,
 *       "tempoMedio": 250,
 *       "tempoMinimo": 100,
 *       "tempoMaximo": 500,
 *       "percentualSucesso": 98.5
 *     }
 *   ]
 * }
 * 
 * @param dataReferencia Data de referência para os dados de telemetria
 * @param listaDados Lista de métricas por endpoint da API
 */
public record TelemetriaResponse(
        /**
         * Data de referência para os dados de telemetria coletados.
         * Formato: yyyy-MM-dd
         */
        @JsonProperty("dataReferencia")
        String dataReferencia,
        
        /**
         * Lista contendo as métricas de performance de cada endpoint da API.
         * Cada item representa um endpoint específico com suas estatísticas.
         */
        @JsonProperty("listaDados")
        List<DadosTelemetria> listaDados
) {
    
    /**
     * Record interno que representa as métricas de telemetria de um endpoint específico.
     * 
     * Contém informações detalhadas sobre performance, uso e confiabilidade
     * de um endpoint da API durante o período de referência.
     * 
     * As métricas incluem:
     * - Nome/identificação do endpoint
     * - Quantidade total de requisições
     * - Tempos de resposta (médio, mínimo, máximo)
     * - Taxa de sucesso das requisições
     * 
     * @param nomeApi Nome ou identificação do endpoint da API
     * @param qtdRequisicoes Quantidade total de requisições recebidas
     * @param tempoMedio Tempo médio de resposta em milissegundos
     * @param tempoMinimo Menor tempo de resposta registrado em milissegundos
     * @param tempoMaximo Maior tempo de resposta registrado em milissegundos
     * @param percentualSucesso Percentual de requisições bem-sucedidas (0-100)
     */
    public record DadosTelemetria(
            /**
             * Nome ou identificação do endpoint da API.
             * Exemplo: "POST /api/simulacao", "GET /api/simulacao/buscar"
             */
            @JsonProperty("nomeApi")
            String nomeApi,
            
            /**
             * Quantidade total de requisições recebidas pelo endpoint
             * durante o período de referência.
             */
            @JsonProperty("qtdRequisicoes")
            Integer qtdRequisicoes,
            
            /**
             * Tempo médio de resposta do endpoint em milissegundos.
             * Calculado com base em todas as requisições do período.
             */
            @JsonProperty("tempoMedio")
            Integer tempoMedio,
            
            /**
             * Menor tempo de resposta registrado para o endpoint
             * durante o período, em milissegundos.
             */
            @JsonProperty("tempoMinimo")
            Integer tempoMinimo,
            
            /**
             * Maior tempo de resposta registrado para o endpoint
             * durante o período, em milissegundos.
             */
            @JsonProperty("tempoMaximo")
            Integer tempoMaximo,
            
            /**
             * Percentual de requisições bem-sucedidas (status 2xx)
             * em relação ao total de requisições. Valor entre 0 e 100.
             */
            @JsonProperty("percentualSucesso")
            Double percentualSucesso
    ) {}
}