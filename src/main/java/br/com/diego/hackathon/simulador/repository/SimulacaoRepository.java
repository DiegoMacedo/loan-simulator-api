package br.com.diego.hackathon.simulador.repository;

import br.com.diego.hackathon.simulador.model.Simulacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para gerenciar operações de persistência da entidade Simulacao.
 * 
 * Esta interface estende JpaRepository fornecendo operações CRUD básicas
 * e define queries customizadas para consultas específicas de simulações
 * de crédito. Utiliza JPQL (Java Persistence Query Language) para queries
 * complexas e métodos derivados do Spring Data JPA para consultas simples.
 * 
 * Principais funcionalidades:
 * - Consultas por produto e período de data
 * - Busca por intervalos de tempo
 * - Ordenação por data de simulação
 * - Contagem de registros
 * - Busca por identificadores únicos
 */
@Repository
public interface SimulacaoRepository extends JpaRepository<Simulacao, Long> {

    /**
     * Busca simulações filtradas por código do produto e intervalo de data.
     * 
     * Esta query JPQL personalizada combina filtros de produto e período temporal,
     * sendo essencial para relatórios e consultas históricas. A ordenação
     * descendente por data garante que simulações mais recentes apareçam primeiro.
     * 
     * Utilizada principalmente pelo endpoint GET /api/simulacoes/produto/{codigo}/data/{data}
     * para consultar simulações de um produto específico em uma data.
     * 
     * @param codigoProduto Código identificador do produto de crédito
     * @param dataInicio Data/hora de início do período (inclusive)
     * @param dataFim Data/hora de fim do período (exclusive)
     * @return Lista de simulações ordenadas por data (mais recentes primeiro)
     */
    @Query("SELECT s FROM Simulacao s WHERE s.codigoProduto = :codigoProduto " +
           "AND s.dataSimulacao >= :dataInicio AND s.dataSimulacao < :dataFim " +
           "ORDER BY s.dataSimulacao DESC")
    List<Simulacao> findByCodigoProdutoAndData(@Param("codigoProduto") Integer codigoProduto, 
                                               @Param("dataInicio") LocalDateTime dataInicio,
                                               @Param("dataFim") LocalDateTime dataFim);

    /**
     * Busca simulações realizadas em um período específico de tempo.
     * 
     * Query JPQL que utiliza BETWEEN para buscar registros em um intervalo
     * de datas. Útil para relatórios gerais, análises temporais e
     * monitoramento de volume de simulações por período.
     * 
     * @param dataInicio Data/hora de início do período (inclusive)
     * @param dataFim Data/hora de fim do período (inclusive)
     * @return Lista de simulações do período ordenadas por data (mais recentes primeiro)
     */
    @Query("SELECT s FROM Simulacao s WHERE s.dataSimulacao BETWEEN :dataInicio AND :dataFim " +
           "ORDER BY s.dataSimulacao DESC")
    List<Simulacao> findByDataSimulacaoBetween(@Param("dataInicio") LocalDateTime dataInicio,
                                               @Param("dataFim") LocalDateTime dataFim);

    /**
     * Busca todas as simulações de um produto específico.
     * 
     * Método derivado do Spring Data JPA que utiliza convenção de nomenclatura
     * para gerar automaticamente a query SQL. A ordenação descendente por data
     * garante que simulações mais recentes apareçam primeiro.
     * 
     * Útil para análises de produto, histórico completo e relatórios específicos.
     * 
     * @param codigoProduto Código identificador do produto
     * @return Lista de todas as simulações do produto ordenadas por data
     */
    List<Simulacao> findByCodigoProdutoOrderByDataSimulacaoDesc(Integer codigoProduto);

    /**
     * Conta o número total de simulações registradas no sistema.
     * 
     * Query de agregação JPQL que retorna a contagem total de registros
     * na tabela de simulações. Essencial para métricas de uso, relatórios
     * de volume e monitoramento do sistema.
     * 
     * @return Número total de simulações realizadas
     */
    @Query("SELECT COUNT(s) FROM Simulacao s")
    Long countTotalSimulacoes();

    /**
     * Busca uma simulação específica pelo seu identificador único.
     * 
     * Método derivado que busca por ID de simulação (UUID gerado automaticamente).
     * Usado para recuperar simulações específicas, validações e consultas
     * de detalhes de uma simulação já realizada.
     * 
     * @param idSimulacao Identificador único da simulação (UUID)
     * @return Simulacao encontrada ou null se não existir
     */
    Simulacao findByIdSimulacao(String idSimulacao);
}