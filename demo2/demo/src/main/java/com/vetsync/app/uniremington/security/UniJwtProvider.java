package com.vetsync.app.uniremington.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * Proveedor JWT dedicado al módulo "Uniremington al Parque".
 *
 * Diferencias con el JwtProvider de VetSync:
 *  - Usa la propiedad {@code uniremington.jwt.secret}
 *  - Permite añadir claims personalizados (documento, facultad, programa)
 *  - El subject del token es el documento del usuario (no el email)
 */
@Slf4j
@Component
public class UniJwtProvider {

    @Value("${uniremington.jwt.secret}")
    private String jwtSecret;

    @Value("${uniremington.jwt.expiration-ms}")
    private long jwtExpirationMs;

    // ── Clave de firma ───────────────────────────────────────────────

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ── Generación ───────────────────────────────────────────────────

    /**
     * Genera un JWT cuyo subject es el documento del usuario.
     * Se añaden como claims: facultad y programa.
     */
    public String generateToken(String documento, Map<String, Object> extraClaims) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);

        JwtBuilder builder = Jwts.builder()
                .setSubject(documento)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey());

        if (extraClaims != null && !extraClaims.isEmpty()) {
            builder.addClaims(extraClaims);
        }

        return builder.compact();
    }

    // ── Validación ───────────────────────────────────────────────────

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("[Uniremington] JWT inválido: {}", e.getMessage());
            return false;
        }
    }

    // ── Extracción de datos ──────────────────────────────────────────

    public String getDocumentoFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public String getFacultadFromToken(String token) {
        return getClaims(token).get("facultad", String.class);
    }

    public long getExpirationMs() {
        return jwtExpirationMs;
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
