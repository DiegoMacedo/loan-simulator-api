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

@RestController // Anotação que combina @Controller e @ResponseBody, ideal para APIs REST
@RequestMapping("/api/simulacoes") // Mapeia todas as requisições que começam com /api/simulacoes para este controller
public class SimulacaoController {

    private final SimulacaoService simulacaoService;

    @Autowired // Injeta a dependência do nosso serviço
    public SimulacaoController(SimulacaoService simulacaoService) {
        this.simulacaoService = simulacaoService;
    }

    // Mapeia requisições do tipo POST para /api/simulacoes
    @PostMapping
    public ResponseEntity<SimulacaoResponse> criarSimulacao(@Valid @RequestBody SimulacaoRequest request) {
        // @RequestBody: Converte o corpo da requisição (JSON) para o nosso objeto SimulacaoRequest

        // Chama o método do serviço para executar a lógica de negócio
        SimulacaoResponse response = simulacaoService.simular(request);

        // Retorna a resposta com status HTTP 200 OK e o corpo em JSON
        return ResponseEntity.ok(response);
    }

    // Endpoint para buscar simulações por produto e data
    @GetMapping("/produto/{codigoProduto}/data/{dataReferencia}")
    public ResponseEntity<SimulacoesPorProdutoResponse> buscarSimulacoesPorProdutoEData(
            @PathVariable Integer codigoProduto,
            @PathVariable String dataReferencia) {
        
        LocalDate data = LocalDate.parse(dataReferencia, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        SimulacoesPorProdutoResponse response = simulacaoService.buscarSimulacoesPorProdutoEData(codigoProduto, data);
        
        return ResponseEntity.ok(response);
    }

    // Endpoint para retornar dados de telemetria
    @GetMapping("/telemetria/{dataReferencia}")
    public ResponseEntity<TelemetriaResponse> obterTelemetria(@PathVariable String dataReferencia) {
        
        LocalDate data = LocalDate.parse(dataReferencia, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        TelemetriaResponse response = simulacaoService.obterTelemetria(data);
        
        return ResponseEntity.ok(response);
    }
}