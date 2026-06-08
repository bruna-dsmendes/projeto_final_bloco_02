package com.generation.farmacia.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {

    @Value("${jwt.secret:5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437}")
    private String secret;

    private static final long ACCESS_TOKEN_EXPIRATION  = 1000L * 60 * 60;       // 1 hora
    private static final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 7; // 7 dias


    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

   
    // ACCESS TOKEN
        public String generateToken(String userName) {
        return createToken(new HashMap<>(), userName, ACCESS_TOKEN_EXPIRATION);
    }

    public String generateTokenWithRole(String userName, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, userName, ACCESS_TOKEN_EXPIRATION);
    }

    private String createToken(Map<String, Object> claims, String userName, long expirationMs) {
        return Jwts.builder()
                .claims(claims)
                .subject(userName)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSignKey())   // nova API — sem SignatureAlgorithm deprecated
                .compact();
    }

    // Refresh Token salvo no banco
    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    // Extração de Claims
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // VALIDAÇÃO
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}
