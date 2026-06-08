package com.generation.farmacia.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.farmacia.model.Usuario;
import com.generation.farmacia.model.UsuarioLogin;
import com.generation.farmacia.repository.UsuarioRepository;
import com.generation.farmacia.security.JwtService;
import com.generation.farmacia.security.LoginRateLimiter;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    // Rate limiter injetado
    @Autowired
    private LoginRateLimiter rateLimiter;

    // CADASTRO
    public Optional<Usuario> cadastrarUsuario(Usuario usuario) {
        if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent()) {
            return Optional.empty();
        }
        // Role padrão: USER — ADMIN só pode ser definido manualmente no banco
        if (usuario.getRole() == null) {
            usuario.setRole(Usuario.Role.USER);
        }
        usuario.setSenha(criptografarSenha(usuario.getSenha()));
        return Optional.of(usuarioRepository.save(usuario));
    }

    // LOGIN com Rate Limiting + Refresh Token
    public Optional<UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin) {

        if (usuarioLogin.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados de login não informados.");
        }

        UsuarioLogin login = usuarioLogin.get();

        if (login.getUsuario() == null || login.getSenha() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário e senha são obrigatórios.");
        }

        // verifica bloqueio por tentativas excessivas
        if (rateLimiter.estaBloqueado(login.getUsuario())) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                "Conta temporariamente bloqueada por excesso de tentativas. Tente novamente em 15 minutos.");
        }

        try {
            var credentials = new UsernamePasswordAuthenticationToken(
                    login.getUsuario(), login.getSenha());

            Authentication authentication = authenticationManager.authenticate(credentials);

            if (authentication.isAuthenticated()) {
                Optional<Usuario> usuario = usuarioRepository.findByUsuario(login.getUsuario());

                if (usuario.isPresent()) {
                    // Login bem-sucedido — limpa o contador de falhas
                    rateLimiter.registrarSucesso(login.getUsuario());

                    String role = usuario.get().getRole().name();

                    // Gera access token com a role embutida
                    String accessToken = jwtService.generateTokenWithRole(login.getUsuario(), role);

                    // MELHORIA: gera refresh token e persiste no banco
                    String refreshToken = jwtService.generateRefreshToken();
                    usuario.get().setRefreshToken(refreshToken);
                    usuarioRepository.save(usuario.get());

                    login.setId(usuario.get().getId());
                    login.setNome(usuario.get().getNome());
                    login.setRole(role);
                    login.setToken(accessToken);
                    login.setRefreshToken(refreshToken);
                    login.setSenha(""); // nunca retornar a senha

                    return usuarioLogin;
                }
            }
        } catch (BadCredentialsException e) {
            // registra tentativa falha para o rate limiter
            rateLimiter.registrarFalha(login.getUsuario());
            int restantes = rateLimiter.tentativasRestantes(login.getUsuario());

            String msg = restantes > 0
                    ? "Usuário ou senha incorretos. Tentativas restantes: " + restantes
                    : "Conta bloqueada por 15 minutos após excesso de tentativas.";

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, msg);
        }

        return Optional.empty();
    }

    // REFRESH TOKEN — renova o access token sem novo login
    public Optional<UsuarioLogin> refreshToken(String refreshToken) {
        Optional<Usuario> usuario = usuarioRepository.findByRefreshToken(refreshToken);

        if (usuario.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido ou expirado.");
        }

        String role = usuario.get().getRole().name();
        String novoAccessToken = jwtService.generateTokenWithRole(usuario.get().getUsuario(), role);

        // Roda o refresh token a cada uso (mais seguro)
        String novoRefreshToken = jwtService.generateRefreshToken();
        usuario.get().setRefreshToken(novoRefreshToken);
        usuarioRepository.save(usuario.get());

        UsuarioLogin resposta = new UsuarioLogin();
        resposta.setId(usuario.get().getId());
        resposta.setNome(usuario.get().getNome());
        resposta.setUsuario(usuario.get().getUsuario());
        resposta.setRole(role);
        resposta.setToken(novoAccessToken);
        resposta.setRefreshToken(novoRefreshToken);
        resposta.setSenha("");

        return Optional.of(resposta);
    }

    private String criptografarSenha(String senha) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(senha);
    }
}
