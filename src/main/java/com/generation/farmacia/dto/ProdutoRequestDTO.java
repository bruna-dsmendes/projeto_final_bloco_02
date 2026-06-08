package com.generation.farmacia.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProdutoRequestDTO(
    @NotBlank(message = "O nome do produto é obrigatório.")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
    String nome,

    @Size(max = 255, message = "A descrição não pode passar de 255 caracteres.")
    String descricao,

    @NotNull(message = "O preço é obrigatório.")
    @Positive(message = "O preço deve ser maior que zero.")
    BigDecimal preco,

    @NotNull(message = "A quantidade em estoque é obrigatória.")
    Integer quantidade,

    @NotNull(message = "O ID da categoria é obrigatório.")
    Long categoriaId
) {}