package com.example.ECommerceBackend.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;
    public String generateToken(String email,Long userId,String role)
    {
        return Jwts.builder()
                .subject(email)
                .claim("userId",userId)
                .claim("role",role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+expirationMs))
                .signWith(getSignedKey())
                .compact();
    }

    private SecretKey getSignedKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public Claims verifySignatureAndExtractClaims(String token)
    {
        return Jwts.parser()
                .verifyWith(getSignedKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token)
    {
        return verifySignatureAndExtractClaims(token).getSubject();
    }
    public Date getExpiration(String token) {
        return verifySignatureAndExtractClaims(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }
}
