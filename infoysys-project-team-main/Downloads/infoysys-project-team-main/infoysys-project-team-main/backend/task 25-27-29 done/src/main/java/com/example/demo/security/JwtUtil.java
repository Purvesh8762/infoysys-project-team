package com.example.demo.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String secret;
    private final long expirationMs;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expirationMs}") long expirationMs) {
        this.secret = secret;
        this.expirationMs = expirationMs;
    }

    // -----------------------------
    // ACCESS TOKEN
    // -----------------------------
    public String generateToken(String email, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }

    // -----------------------------
    // REFRESH TOKEN (7 days)
    // -----------------------------
    public String generateRefreshToken(String email) {
        long refreshValidityMs = 7L * 24 * 60 * 60 * 1000; // 7 days
        Date now = new Date();
        Date exp = new Date(now.getTime() + refreshValidityMs);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }

    // -----------------------------
    // PARSE CLAIMS
    // -----------------------------
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

    // -----------------------------
    // VALIDATION FOR ACCESS TOKEN
    // -----------------------------
    public boolean isTokenExpired(String token) {
        try {
            Date exp = parseClaims(token).getExpiration();
            return exp.before(new Date());
        } catch (ExpiredJwtException ex) {
            return true;
        }
    }

    // -----------------------------
    // TASK 27: VALIDATE REFRESH TOKEN
    // allows expired tokens but checks signature
    // -----------------------------
    public boolean validateTokenIgnoreExpiry(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secret.getBytes())
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            return true; // signature OK → acceptable for refresh
        } catch (Exception ex) {
            return false;
        }
    }

    // -----------------------------
    // EXTRACT EMAIL / ROLE
    // -----------------------------
    public String extractEmail(String token) {
        try {
            return parseClaims(token).getSubject();
        } catch (ExpiredJwtException ex) {
            return ex.getClaims().getSubject();
        }
    }

    public String extractRole(String token) {
        try {
            Object r = parseClaims(token).get("role");
            return r == null ? null : r.toString();
        } catch (ExpiredJwtException ex) {
            Object r = ex.getClaims().get("role");
            return r == null ? null : r.toString();
        }
    }
}
