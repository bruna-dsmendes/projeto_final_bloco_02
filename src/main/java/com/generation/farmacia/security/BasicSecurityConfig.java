package com.generation.farmacia.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity

@EnableMethodSecurity(prePostEnabled = true)
public class BasicSecurityConfig {

    @Autowired
    private JwtAuthFilter authFilter;

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Rotas públicas — sem token
                .requestMatchers("/usuarios/logar").permitAll()
                .requestMatchers("/usuarios/cadastrar").permitAll()
                .requestMatchers("/usuarios/refresh").permitAll()

                // GETs de produtos e categorias são públicos
                .requestMatchers(HttpMethod.GET, "/produtos/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/categorias/**").permitAll()
                
                .requestMatchers(HttpMethod.POST, "/produtos").permitAll()

                // Post, put e delete em produtos/categorias exige role ADMIN
                .requestMatchers(HttpMethod.POST,   "/produtos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/produtos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/produtos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,   "/categorias/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/categorias/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/categorias/**").hasRole("ADMIN")

                // Qualquer outra rota exige autenticação
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
