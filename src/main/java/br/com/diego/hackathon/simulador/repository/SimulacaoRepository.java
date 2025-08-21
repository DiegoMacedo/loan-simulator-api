package br.com.diego.hackathon.simulador.repository;

import br.com.diego.hackathon.simulador.model.Simulacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SimulacaoRepository extends JpaRepository<Simulacao, Long> {

    /**
     * Busca simulações por código do produto e data específica
     */
    @Query("SELECT s FROM Simulacao s WHERE s.codigoProduto = :codigoProduto " +
           "AND s.dataSimulacao >= :dataInicio AND s.dataSimulacao < :dataFim " +
           "ORDER BY s.dataSimulacao DESC")
    List<Simulacao> findByCodigoProdutoAndData(@Param("codigoProduto") Integer codigoProduto, 
                                               @Param("dataInicio") LocalDateTime dataInicio,
                                               @Param("dataFim") LocalDateTime dataFim);

    /**
     * Busca simulações por período de data
     */
    @Query("SELECT s FROM Simulacao s WHERE s.dataSimulacao BETWEEN :dataInicio AND :dataFim " +
           "ORDER BY s.dataSimulacao DESC")
    List<Simulacao> findByDataSimulacaoBetween(@Param("dataInicio") LocalDateTime dataInicio,
                                               @Param("dataFim") LocalDateTime dataFim);

    /**
     * Busca simulações por código do produto
     */
    List<Simulacao> findByCodigoProdutoOrderByDataSimulacaoDesc(Integer codigoProduto);

    /**
     * Conta total de simulações realizadas
     */
    @Query("SELECT COUNT(s) FROM Simulacao s")
    Long countTotalSimulacoes();

    /**
     * Busca simulações por ID de simulação externa
     */
    Simulacao findByIdSimulacao(String idSimulacao);
}