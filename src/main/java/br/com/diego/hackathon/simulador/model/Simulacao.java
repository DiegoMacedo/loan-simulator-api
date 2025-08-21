package br.com.diego.hackathon.simulador.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Simulacao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Simulacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SIMULACAO")
    private Long id;

    @Column(name = "ID_SIMULACAO_EXTERNA", nullable = false, unique = true)
    private String idSimulacao;

    @Column(name = "VALOR_DESEJADO", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorDesejado;

    @Column(name = "PRAZO", nullable = false)
    private Integer prazo;

    @Column(name = "VALOR_TOTAL_PARCELAS", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotalParcelas;

    @Column(name = "DATA_SIMULACAO", nullable = false)
    private LocalDateTime dataSimulacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PRODUTO", nullable = false)
    private Produto produto;

    @Column(name = "CODIGO_PRODUTO", nullable = false)
    private Integer codigoProduto;

    @Column(name = "DESCRICAO_PRODUTO", nullable = false)
    private String descricaoProduto;

    @Column(name = "TAXA_JUROS", nullable = false, precision = 5, scale = 3)
    private BigDecimal taxaJuros;

    @Column(name = "VALOR_AMORTIZACAO", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorAmortizacao;

    @Column(name = "VALOR_TOTAL_JUROS", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotalJuros;

    @Column(name = "VALOR_TOTAL_GERAL", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotalGeral;
}