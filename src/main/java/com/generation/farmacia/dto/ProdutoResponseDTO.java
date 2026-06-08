package com.generation.farmacia.dto;

import java.math.BigDecimal;
import com.generation.farmacia.model.Produto;

public record ProdutoResponseDTO(
    Long id,
    String nome,
    String descricao,
    BigDecimal preco,
    Integer quantidade,
    Long categoriaId,
    String categoriaNome
) {
    // Construtor que transforma uma Entity em um DTO de resposta
    public ProdutoResponseDTO(Produto produto) {
        this(
            produto.getId(),
            produto.getNome(),
            produto.getDescricao(),
            produto.getPreco(),
            produto.getQuantidade(),
            produto.getCategoria() != null ? produto.getCategoria().getId() : null,
            produto.getCategoria() != null ? produto.getCategoria().getNome() : "Sem Categoria"
        );
    }
}