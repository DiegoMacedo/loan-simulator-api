# Guia de Testes - API Simulador de Financiamento

## Visão Geral
A API do Simulador de Financiamento está rodando em `http://localhost:8080` e oferece simulações de financiamento com diferentes produtos.

## Endpoint Principal

### POST /api/simulacoes
Realiza simulação de financiamento baseada no valor desejado e prazo.

**URL:** `http://localhost:8080/api/simulacoes`

**Método:** POST

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "valorDesejado": 10000,
  "prazo": 12
}
```

## Produtos Disponíveis

A API possui 4 produtos configurados:

1. **Crédito Pessoal** (ID: 1)
   - Valor: R$ 1.000 - R$ 100.000
   - Prazo: 6 - 60 meses
   - Taxa: 2,5% ao mês

2. **Financiamento Veicular** (ID: 2)
   - Valor: R$ 50.000 - R$ 500.000
   - Prazo: 12 - 84 meses
   - Taxa: 2,0% ao mês

3. **Financiamento Imobiliário** (ID: 3)
   - Valor: R$ 100.000 - R$ 2.000.000
   - Prazo: 120 - 420 meses
   - Taxa: 1,0% ao mês

4. **Crédito Consignado** (ID: 4)
   - Valor: R$ 500 - R$ 50.000
   - Prazo: 6 - 96 meses
   - Taxa: 1,5% ao mês

## Exemplos de Teste

### Teste 1: Crédito Pessoal
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/simulacoes" -Method POST -Headers @{"Content-Type"="application/json"} -Body '{"valorDesejado": 10000, "prazo": 12}'
```

### Teste 2: Financiamento Veicular
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/simulacoes" -Method POST -Headers @{"Content-Type"="application/json"} -Body '{"valorDesejado": 80000, "prazo": 48}'
```

### Teste 3: Valor/Prazo Inválido (Erro 404)
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/simulacoes" -Method POST -Headers @{"Content-Type"="application/json"} -Body '{"valorDesejado": 150000, "prazo": 36}'
```

## Estrutura da Resposta

### Resposta de Sucesso (200)
```json
{
  "nomeProduto": "Crédito Pessoal",
  "resultados": [
    {
      "tipo": "SAC",
      "parcelas": [
        {
          "numero": 1,
          "valorAmortizacao": 833.33,
          "valorJuros": 250.00,
          "valorPrestacao": 1083.33
        },
        {
          "numero": 2,
          "valorAmortizacao": 833.33,
          "valorJuros": 229.17,
          "valorPrestacao": 1062.50
        }
        // ... demais parcelas
      ]
    },
    {
      "tipo": "PRICE",
      "parcelas": [
        // ... parcelas do sistema PRICE
      ]
    }
  ]
}
```

### Resposta de Erro (404)
Quando não há produto disponível para o valor/prazo solicitado.

## Console H2

Para verificar os dados no banco:
- **URL:** `http://localhost:8080/h2-console`
- **JDBC URL:** `jdbc:h2:mem:testdb`
- **Username:** `sa`
- **Password:** (deixar em branco)

## Actuator

Monitoramento da aplicação:
- **URL:** `http://localhost:8080/actuator`

## Status da Aplicação

✅ Aplicação iniciada com sucesso
✅ Banco H2 configurado e funcionando
✅ Dados de teste carregados
✅ API respondendo corretamente
✅ Diferentes produtos sendo selecionados conforme valor/prazo
✅ Tratamento de erros funcionando (404 para valores inválidos)

## Observações

- A aplicação seleciona automaticamente o produto mais adequado baseado no valor e prazo solicitados
- Retorna simulações nos sistemas SAC e PRICE
- Valores e prazos fora dos limites dos produtos retornam erro 404
- A taxa de juros é aplicada mensalmente sobre o saldo devedor