package br.com.diego.hackathon.simulador.controller;

import br.com.diego.hackathon.simulador.dto.SimulacaoRequest;
import br.com.diego.hackathon.simulador.dto.SimulacaoResponse;
import br.com.diego.hackathon.simulador.dto.SimulacoesPorProdutoResponse;
import br.com.diego.hackathon.simulador.dto.TelemetriaResponse;
import br.com.diego.hackathon.simulador.service.SimulacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Controller REST responsável por gerenciar os endpoints da API de Simulação de Crédito.
 * 
 * Esta classe expõe três principais funcionalidades:
 * 1. Criação de simulações de crédito com cálculo de parcelas
 * 2. Consulta de simulações por produto e data
 * 3. Obtenção de dados de telemetria do sistema
 * 
 * Todos os endpoints retornam dados em formato JSON e seguem os padrões REST.
 * 
 * @author Diego
 * @version 1.0
 * @since 2023
 */
@RestController // Anotação que combina @Controller e @ResponseBody, ideal para APIs REST
@RequestMapping("/api/simulacoes") // Mapeia todas as requisições que começam com /api/simulacoes para este controller
public class SimulacaoController {

    private final SimulacaoService simulacaoService;

    /**
     * Construtor para injeção de dependência do serviço de simulação.
     * 
     * @param simulacaoService Serviço responsável pela lógica de negócio das simulações
     */
    @Autowired // Injeta a dependência do nosso serviço
    public SimulacaoController(SimulacaoService simulacaoService) {
        this.simulacaoService = simulacaoService;
    }

    /**
     * Endpoint para criar uma nova simulação de crédito.
     * 
     * Este método recebe os dados da simulação (valor desejado e prazo),
     * calcula automaticamente as parcelas, juros e amortização,
     * persiste a simulação no banco de dados e retorna o resultado completo.
     * 
     * Exemplo de uso:
     * POST /api/simulacoes
     * {
     *   "valorDesejado": 10000.00,
     *   "prazo": 12
     * }
     * 
     * @param request Dados da simulação (valor desejado e prazo em meses)
     * @return ResponseEntity contendo os detalhes da simulação calculada
     */
    @PostMapping // Mapeia requisições do tipo POST para /api/simulacoes
    public ResponseEntity<SimulacaoResponse> criarSimulacao(@Valid @RequestBody SimulacaoRequest request) {
        // @Valid: Valida os dados de entrada conforme as anotações no DTO
        // @RequestBody: Converte o corpo da requisição (JSON) para o nosso objeto SimulacaoRequest

        // Chama o método do serviço para executar a lógica de negócio
        SimulacaoResponse response = simulacaoService.simular(request);

        // Retorna a resposta com status HTTP 200 OK e o corpo em JSON
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para buscar simulações filtradas por código do produto e data.
     * 
     * Este método permite consultar todas as simulações realizadas para um
     * produto específico em uma determinada data. Útil para relatórios e
     * análises de histórico de simulações.
     * 
     * Exemplo de uso:
     * GET /api/simulacoes/produto/1/data/2023-07-02
     * 
     * @param codigoProduto Código identificador do produto (ex: 1, 2, 3)
     * @param dataReferencia Data no formato yyyy-MM-dd (ex: 2023-07-02)
     * @return ResponseEntity contendo lista de simulações encontradas
     */
    @GetMapping("/produto/{codigoProduto}/data/{dataReferencia}")
    public ResponseEntity<SimulacoesPorProdutoResponse> buscarSimulacoesPorProdutoEData(
            @PathVariable Integer codigoProduto, // @PathVariable: Extrai o valor da URL
            @PathVariable String dataReferencia) {
        
        // Converte a string da data para LocalDate usando o formato padrão ISO
        LocalDate data = LocalDate.parse(dataReferencia, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        // Chama o serviço para buscar as simulações filtradas
        SimulacoesPorProdutoResponse response = simulacaoService.buscarSimulacoesPorProdutoEData(codigoProduto, data);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para obter dados de telemetria do sistema.
     * 
     * Este método retorna informações sobre o estado atual do sistema,
     * incluindo uso de memória, CPU e espaço em disco. Útil para
     * monitoramento e diagnóstico da aplicação.
     * 
     * Exemplo de uso:
     * GET /api/simulacoes/telemetria/2023-07-02
     * 
     * @param dataReferencia Data de referência no formato yyyy-MM-dd
     * @return ResponseEntity contendo dados de telemetria do sistema
     */
    @GetMapping("/telemetria/{dataReferencia}")
    public ResponseEntity<TelemetriaResponse> obterTelemetria(@PathVariable String dataReferencia) {
        
        // Converte a string da data para LocalDate
        LocalDate data = LocalDate.parse(dataReferencia, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        // Chama o serviço para obter os dados de telemetria
        TelemetriaResponse response = simulacaoService.obterTelemetria(data);
        
        return ResponseEntity.ok(response);
    }
}