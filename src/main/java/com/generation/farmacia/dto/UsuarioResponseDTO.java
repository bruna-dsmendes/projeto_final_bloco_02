package com.generation.farmacia.dto;

public record UsuarioResponseDTO(
    Long id,
    String nome,
    String usuario, // e-mail
    String foto
) {}