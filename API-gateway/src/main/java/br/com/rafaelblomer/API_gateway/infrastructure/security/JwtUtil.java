package br.com.rafaelblomer.API_gateway.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    // Chave secreta codificada em Base64 (precisa ter pelo menos 256 bits para HS256)
    private final String base64SecretKey = "c2VjdXJlLWNoYXJhY3Rlci1zdXBlci1zZWFjdXJlLXdpdGgtYS1saW5nLXRleHQ=";

    // Decodifica a chave e cria um SecretKey
    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(base64SecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extrai as claims do token
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Extrai o e-mail (subject) do token
    public String extrairEmailToken(String token) {
        return extractClaims(token).getSubject();
    }

    // Verifica se o token expirou
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Valida o token
    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
