package br.com.diego.hackathon.simulador.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade JPA que representa uma simulação de crédito no sistema.
 * 
 * Esta classe mapeia a tabela "Simulacao" no banco de dados e armazena
 * todas as informações relacionadas a uma simulação de empréstimo,
 * incluindo dados de entrada (valor desejado, prazo), dados do produto
 * selecionado e resultados dos cálculos financeiros.
 * 
 * Utiliza BigDecimal para campos monetários garantindo precisão nos
 * cálculos financeiros e evitando problemas de arredondamento.
 * 
 * Relacionamentos:
 * - ManyToOne com Produto: Uma simulação pertence a um produto específico
 * 
 * Anotações Lombok:
 * - @Data: Gera getters, setters, toString, equals e hashCode
 * - @NoArgsConstructor: Construtor sem argumentos (requerido pelo JPA)
 * - @AllArgsConstructor: Construtor com todos os argumentos
 */
@Entity
@Table(name = "Simulacao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Simulacao {

    /**
     * Chave primária da simulação (ID interno do banco de dados).
     * Gerada automaticamente usando estratégia IDENTITY.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SIMULACAO")
    private Long id;

    /**
     * Identificador único externo da simulação (UUID).
     * Usado para identificar a simulação em APIs e integrações externas.
     * Campo obrigatório e único no sistema.
     */
    @Column(name = "ID_SIMULACAO_EXTERNA", nullable = false, unique = true)
    private String idSimulacao;

    /**
     * Valor monetário desejado pelo cliente para o empréstimo.
     * Campo obrigatório com precisão de 15 dígitos e 2 casas decimais.
     * Representa o valor principal que o cliente deseja solicitar.
     */
    @Column(name = "VALOR_DESEJADO", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorDesejado;

    /**
     * Prazo desejado para pagamento em meses.
     * Campo obrigatório que define o número de parcelas do empréstimo.
     * Usado nos cálculos de amortização SAC e Tabela Price.
     */
    @Column(name = "PRAZO", nullable = false)
    private Integer prazo;

    /**
     * Valor total de todas as parcelas do empréstimo.
     * Calculado automaticamente durante a simulação.
     * Representa a soma de todas as parcelas (principal + juros).
     */
    @Column(name = "VALOR_TOTAL_PARCELAS", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotalParcelas;

    /**
     * Data e hora em que a simulação foi realizada.
     * Campo obrigatório usado para auditoria e consultas históricas.
     * Definido automaticamente no momento da criação da simulação.
     */
    @Column(name = "DATA_SIMULACAO", nullable = false)
    private LocalDateTime dataSimulacao;

    /**
     * Relacionamento Many-to-One com a entidade Produto.
     * Representa o produto de crédito selecionado para esta simulação.
     * Carregamento lazy para otimizar performance nas consultas.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PRODUTO", nullable = false)
    private Produto produto;

    /**
     * Código identificador do produto de crédito.
     * Campo desnormalizado para facilitar consultas e relatórios.
     * Evita joins desnecessários em consultas simples.
     */
    @Column(name = "CODIGO_PRODUTO", nullable = false)
    private Integer codigoProduto;

    /**
     * Descrição textual do produto de crédito.
     * Campo desnormalizado que armazena o nome/descrição do produto
     * no momento da simulação, preservando histórico mesmo se o produto for alterado.
     */
    @Column(name = "DESCRICAO_PRODUTO", nullable = false)
    private String descricaoProduto;

    /**
     * Taxa de juros aplicada na simulação (em decimal).
     * Precisão de 5 dígitos com 3 casas decimais (ex: 12.500% = 0.125).
     * Valor fixo do produto no momento da simulação.
     */
    @Column(name = "TAXA_JUROS", nullable = false, precision = 5, scale = 3)
    private BigDecimal taxaJuros;

    /**
     * Valor da amortização calculado para a simulação.
     * Representa o valor principal que será pago mensalmente.
     * Calculado diferentemente para SAC e Tabela Price.
     */
    @Column(name = "VALOR_AMORTIZACAO", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorAmortizacao;

    /**
     * Valor total dos juros que serão pagos durante todo o empréstimo.
     * Calculado como: (valor total das parcelas - valor desejado).
     * Representa o custo financeiro total do empréstimo.
     */
    @Column(name = "VALOR_TOTAL_JUROS", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotalJuros;

    /**
     * Valor total geral do empréstimo (principal + juros).
     * Equivale ao valor total das parcelas.
     * Representa o montante total que será pago pelo cliente.
     */
    @Column(name = "VALOR_TOTAL_GERAL", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotalGeral;
}