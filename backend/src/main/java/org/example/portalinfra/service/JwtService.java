
package org.example.portalinfra.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;

    public JwtService(
            @Value("${JWT_SECRET}") String jwtSecret) {

        byte[] keyBytes =
                jwtSecret.getBytes(StandardCharsets.UTF_8);

        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String gerarToken(Long usuarioId) {
        Instant agora = Instant.now();
        

        return Jwts.builder()
                .subject(usuarioId.toString())
                .issuedAt(Date.from(agora))
                .expiration(
                        Date.from(agora.plus(1, ChronoUnit.HOURS))
                )
                .signWith(secretKey)
                .compact();
    }

    public Long extrairUsuarioId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validarToken(String token) {
        try {
            extrairUsuarioId(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
