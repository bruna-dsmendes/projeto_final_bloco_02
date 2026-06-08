package com.generation.farmacia.security;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

//Rate Limiting de login
 
 //Bloqueia temporariamente um usuário após 5 tentativas falhas consecutivas.
 //A janela de bloqueio é de 15 minutos.
 
@Component
public class LoginRateLimiter {

    private static final int  MAX_TENTATIVAS    = 5;
    private static final long JANELA_BLOQUEIO_MS = 15L * 60 * 1000; // 15 minutos

    private record TentativaInfo(int contador, Instant bloqueadoAte) {}

    private final Map<String, TentativaInfo> tentativas = new ConcurrentHashMap<>();

    
    // Verifica se o usuário está bloqueado.
  
    public boolean estaBloqueado(String email) {
        TentativaInfo info = tentativas.get(email.toLowerCase());
        if (info == null) return false;

        // Se o bloqueio já expirou, limpa o registro
        if (info.bloqueadoAte() != null && Instant.now().isAfter(info.bloqueadoAte())) {
            tentativas.remove(email.toLowerCase());
            return false;
        }

        return info.bloqueadoAte() != null;
    }

    
     // Ao atingir MAX_TENTATIVAS, aplica bloqueio de JANELA_BLOQUEIO_MS.
    public void registrarFalha(String email) {
        String key = email.toLowerCase();
        TentativaInfo atual = tentativas.getOrDefault(key, new TentativaInfo(0, null));

        int novoContador = atual.contador() + 1;

        if (novoContador >= MAX_TENTATIVAS) {
            Instant bloqueioAte = Instant.now().plusMillis(JANELA_BLOQUEIO_MS);
            tentativas.put(key, new TentativaInfo(novoContador, bloqueioAte));
        } else {
            tentativas.put(key, new TentativaInfo(novoContador, null));
        }
    }

    
     // Limpa o contador após login bem-sucedido.
    public void registrarSucesso(String email) {
        tentativas.remove(email.toLowerCase());
    }

    
    //Retorna quantas tentativas restam antes do bloqueio.
    public int tentativasRestantes(String email) {
        TentativaInfo info = tentativas.get(email.toLowerCase());
        if (info == null) return MAX_TENTATIVAS;
        return Math.max(0, MAX_TENTATIVAS - info.contador());
    }
}
