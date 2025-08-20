package br.com.diego.hackathon.simulador.controller;

import br.com.diego.hackathon.simulador.dto.SimulacaoRequest;
import br.com.diego.hackathon.simulador.dto.SimulacaoResponse;
import br.com.diego.hackathon.simulador.service.SimulacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}