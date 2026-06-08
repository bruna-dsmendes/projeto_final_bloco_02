package com.generation.farmacia.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.generation.farmacia.model.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
   
	// Pesquisa todos os produtos com quantidade em estoque menor ou igual ao parâmetro
    List<Produto> findAllByQuantidadeLessThanEqual(Integer quantidade);
}
