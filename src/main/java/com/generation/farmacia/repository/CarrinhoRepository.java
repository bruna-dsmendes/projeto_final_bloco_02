package com.generation.farmacia.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.generation.farmacia.model.Carrinho;

@Repository
public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {
    
    // Método especial para encontrar o carrinho atual de um usuário específico
    Optional<Carrinho> findByUsuarioId(Long usuarioId);
}
