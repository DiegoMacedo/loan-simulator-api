# Script para testar os novos endpoints da API

# Teste do endpoint de simulação existente
Write-Host "Testando endpoint de simulação..."
$simulacaoBody = @{
    valorDesejado = 10000
    prazo = 12
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/simulacoes" -Method POST -Body $simulacaoBody -ContentType "application/json"
    Write-Host "Simulação criada com sucesso:" -ForegroundColor Green
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "Erro na simulação: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n" 

# Teste do endpoint de simulações por produto e data
Write-Host "Testando endpoint de simulações por produto e data..."
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/simulacoes/produto/1/data/2023-07-02" -Method GET
    Write-Host "Simulações por produto encontradas:" -ForegroundColor Green
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "Erro ao buscar simulações: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n"

# Teste do endpoint de telemetria
Write-Host "Testando endpoint de telemetria..."
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/simulacoes/telemetria/2023-07-02" -Method GET
    Write-Host "Dados de telemetria obtidos:" -ForegroundColor Green
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "Erro ao obter telemetria: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nTestes concluídos!"