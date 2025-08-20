package br.com.diego.hackathon.simulador.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "Produto")
@Data
@NoArgsConstructor
public class Produto {

    // Getters manuais para garantir compatibilidade
    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public BigDecimal getValorMinimo() { return valorMinimo; }
    public BigDecimal getValorMaximo() { return valorMaximo; }
    public Integer getPrazoMinimo() { return prazoMinimo; }
    public Integer getPrazoMaximo() { return prazoMaximo; }
    public BigDecimal getTaxaJuros() { return taxaJuros; }

    @Id
    @Column(name = "ID_PRODUTO")
    private Integer id;

    @Column(name = "DS_PRODUTO", nullable = false)
    private String nome;

    @Column(name = "VR_MINIMO", nullable = false)
    private BigDecimal valorMinimo;

    @Column(name = "VR_MAXIMO", nullable = false)
    private BigDecimal valorMaximo;

    @Column(name = "NU_MINIMO_MESES", nullable = false)
    private Integer prazoMinimo;

    @Column(name = "NU_MAXIMO_MESES", nullable = false)
    private Integer prazoMaximo;

    @Column(name = "PC_TAXA_JUROS", nullable = false)
    private BigDecimal taxaJuros;
}