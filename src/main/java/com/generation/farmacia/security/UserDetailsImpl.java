package com.generation.farmacia.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.generation.farmacia.model.Usuario;

public class UserDetailsImpl implements UserDetails {

    private String userName;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Usuario user) {
        this.userName = user.getUsuario();
        this.password = user.getSenha();

        // authority reflete a role real do usuário (ADMIN ou USER)
        // Spring Security exige o prefixo ROLE_ para uso com hasRole()
        String role = (user.getRole() != null)
                ? "ROLE_" + user.getRole().name()
                : "ROLE_USER";

        this.authorities = List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return userName; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
