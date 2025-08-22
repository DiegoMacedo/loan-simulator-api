# API de Simulação de Crédito

API REST para simulação de crédito desenvolvida em Spring Boot, permitindo calcular parcelas, juros e amortização para empréstimos.

## 🚀 Tecnologias Utilizadas

- **Java 17+** - Linguagem de programação
- **Spring Boot 3.3.2** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **Spring Web** - APIs REST
- **H2 Database** - Banco de dados em memória
- **Maven** - Gerenciamento de dependências
- **Jackson** - Serialização JSON

## 📋 Pré-requisitos

- Java 17 ou superior
- Maven 3.6+

## 🔧 Instalação e Execução

1. Clone o repositório:
```bash
git clone <url-do-repositorio>
cd loan-simulator-api
```

2. Execute a aplicação:
```bash
mvn spring-boot:run
```

3. A aplicação estará disponível em: `http://localhost:8080`

## 🌐 Endereço Base da API

```
http://localhost:8080/api
```

## 🔐 Autenticação

Atualmente a API **não possui autenticação** implementada. Todos os endpoints são públicos.

## 📚 Endpoints Disponíveis

### 1. Criar Simulação de Crédito

**POST** `/api/simulacoes`

Cria uma nova simulação de crédito com cálculo automático de parcelas, juros e amortização.

#### Parâmetros de Entrada:
```json
{
  "valorDesejado": 10000.00,
  "prazo": 12
}
```

#### Resposta de Sucesso (200):
```json
{
  "valorTotalSimulacao": 10000.00,
  "valorTotalParcelas": 10300.00,
  "taxaJurosAplicada": 0.03,
  "parcelas": [
    {
      "numeroParcela": 1,
      "valorAmortizacao": 833.33,
      "valorJuros": 25.00,
      "valorPrestacao": 858.33
    }
  ]
}
```

#### Exemplo de Uso:
```bash
curl -X POST http://localhost:8080/api/simulacoes \
  -H "Content-Type: application/json" \
  -d '{"valorDesejado": 5000.00, "prazo": 6}'
```

### 2. Buscar Simulações por Produto e Data

**GET** `/api/simulacoes/produto/{codigoProduto}/data/{dataReferencia}`

Retorna simulações filtradas por código do produto e data específica.

#### Parâmetros:
- `codigoProduto` (path): Código do produto (ex: 1)
- `dataReferencia` (path): Data no formato YYYY-MM-DD (ex: 2023-07-02)

#### Resposta de Sucesso (200):
```json
[
  {
    "id": 1,
    "valorDesejado": 5000.00,
    "prazo": 6,
    "dataSimulacao": "2023-07-02T10:30:00",
    "codigoProduto": 1
  }
]
```

#### Exemplo de Uso:
```bash
curl http://localhost:8080/api/simulacoes/produto/1/data/2023-07-02
```

### 3. Obter Telemetria do Sistema

**GET** `/api/simulacoes/telemetria/{dataReferencia}`

Retorna informações de telemetria do sistema para uma data específica.

#### Parâmetros:
- `dataReferencia` (path): Data no formato YYYY-MM-DD (ex: 2023-07-02)

#### Resposta de Sucesso (200):
```json
{
  "dataConsulta": "2023-07-02",
  "horaConsulta": "14:30:15",
  "memoriaLivre": 512.5,
  "memoriaTotal": 1024.0,
  "percentualCpu": 25.3,
  "espacoLivreDisco": 50.2
}
```

#### Exemplo de Uso:
```bash
curl http://localhost:8080/api/simulacoes/telemetria/2023-07-02
```

## 🏗️ Arquitetura da Aplicação

### Estrutura de Camadas

```
src/main/java/br/com/diego/hackathon/simulador/
├── controller/          # Controladores REST
│   └── SimulacaoController.java
├── service/            # Lógica de negócio
│   └── SimulacaoService.java
├── repository/         # Acesso a dados
│   └── SimulacaoRepository.java
├── model/             # Entidades JPA
│   └── Simulacao.java
├── dto/               # Objetos de transferência
│   ├── SimulacaoRequestDTO.java
│   ├── SimulacaoResponseDTO.java
│   ├── ParcelaDTO.java
│   └── TelemetriaDTO.java
└── SimuladorApplication.java  # Classe principal
```

### Fluxo de Funcionamento

1. **Controller** recebe requisições HTTP
2. **Service** processa lógica de negócio
3. **Repository** gerencia persistência
4. **Model** representa entidades do banco
5. **DTOs** transferem dados entre camadas

## 💾 Banco de Dados

### Configuração H2

- **URL**: `jdbc:h2:mem:testdb`
- **Console H2**: `http://localhost:8080/h2-console`
- **Usuário**: `sa`
- **Senha**: (vazia)

### Tabela Principal

```sql
CREATE TABLE simulacao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    valor_desejado DECIMAL(10,2) NOT NULL,
    prazo INTEGER NOT NULL,
    data_simulacao TIMESTAMP NOT NULL,
    codigo_produto INTEGER NOT NULL
);
```

## 🧮 Lógica de Cálculo

### Fórmulas Utilizadas

- **Taxa de Juros**: 3% ao mês (0.03)
- **Amortização**: Valor principal dividido pelo número de parcelas
- **Juros da Parcela**: Saldo devedor × taxa de juros
- **Prestação**: Amortização + Juros

### Exemplo de Cálculo

Para um empréstimo de R$ 1.000,00 em 3 parcelas:

| Parcela | Saldo Inicial | Amortização | Juros | Prestação | Saldo Final |
|---------|---------------|-------------|-------|-----------|-------------|
| 1       | 1.000,00      | 333,33      | 30,00 | 363,33    | 666,67      |
| 2       | 666,67        | 333,33      | 20,00 | 353,33    | 333,34      |
| 3       | 333,34        | 333,34      | 10,00 | 343,34    | 0,00        |

## 🧪 Testes

### Executar Testes

```bash
mvn test
```

### Teste Manual com PowerShell

```powershell
# Criar simulação
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/simulacoes" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"valorDesejado": 5000.00, "prazo": 6}'

# Buscar simulações
Invoke-RestMethod -Uri "http://localhost:8080/api/simulacoes/produto/1/data/2023-07-02"

# Obter telemetria
Invoke-RestMethod -Uri "http://localhost:8080/api/simulacoes/telemetria/2023-07-02"
```

## 📊 Códigos de Status HTTP

- **200 OK**: Requisição bem-sucedida
- **400 Bad Request**: Dados de entrada inválidos
- **404 Not Found**: Recurso não encontrado
- **500 Internal Server Error**: Erro interno do servidor

## 🔄 Próximas Melhorias

- [ ] Implementar autenticação JWT
- [ ] Adicionar validações de entrada
- [ ] Configurar banco de dados PostgreSQL
- [ ] Implementar cache Redis
- [ ] Adicionar documentação Swagger
- [ ] Implementar logs estruturados
- [ ] Adicionar métricas Prometheus

##  Licença

Este projeto está sob a licença MIT.

##  Contribuição

Contribuições são bem-vindas! Por favor, abra uma issue ou pull request.

---

**Desenvolvido usando Spring Boot**