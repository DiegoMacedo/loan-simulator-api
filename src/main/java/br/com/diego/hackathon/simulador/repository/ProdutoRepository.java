package br.com.diego.hackathon.simulador.repository;

import br.com.diego.hackathon.simulador.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
}