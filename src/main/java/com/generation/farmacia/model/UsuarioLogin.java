package com.generation.farmacia.model;

public class UsuarioLogin {

    private Long id;
    private String nome;
    private String usuario;
    private String senha;
    private String token;

    // Refresh token retornado no login para renovação de sessão
    private String refreshToken;

    //Role retornada no login para o frontend saber as permissões
    private String role;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
