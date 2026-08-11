package com.example.ECommerceBackend.config;

import com.example.ECommerceBackend.services.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("=== JwtFilter TRIGGERED for: " + request.getRequestURI() + " ===");

        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization header received: [" + authHeader + "]");

        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        System.out.println("Token after extraction: [" + token + "]");

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                Claims claims = jwtService.verifySignatureAndExtractClaims(token);
                System.out.println("Signature verified OK. Subject: " + claims.getSubject());

                if (!jwtService.isTokenExpired(token)) {

                    String role=claims.get("role",String.class);
                    List<SimpleGrantedAuthority> authorities=List.of(new SimpleGrantedAuthority("ROLE_"+role));

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("SecurityContext SET. Authenticated = " +
                            SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
                } else {
                    System.out.println("Token is EXPIRED.");
                }
            } catch (Exception e) {
                System.out.println("JWT validation failed: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }
        } else {
            System.out.println("Skipped auth block. Token null? " + (token == null) +
                    " | Existing auth in context? " + (SecurityContextHolder.getContext().getAuthentication() != null));
        }

        filterChain.doFilter(request, response);
    }
}