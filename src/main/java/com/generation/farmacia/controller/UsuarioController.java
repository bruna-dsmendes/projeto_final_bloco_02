package com.generation.farmacia.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.generation.farmacia.model.Usuario;
import com.generation.farmacia.model.UsuarioLogin;
import com.generation.farmacia.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/cadastrar")
    public ResponseEntity<Usuario> cadastrar(@Valid @RequestBody Usuario usuario) {
        return usuarioService.cadastrarUsuario(usuario)
                .map(resposta -> ResponseEntity.status(HttpStatus.CREATED).body(resposta))
                .orElse(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @PostMapping("/logar")
    public ResponseEntity<UsuarioLogin> logar(@RequestBody Optional<UsuarioLogin> usuarioLogin) {
        return usuarioService.autenticarUsuario(usuarioLogin)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<UsuarioLogin> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return usuarioService.refreshToken(refreshToken)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
