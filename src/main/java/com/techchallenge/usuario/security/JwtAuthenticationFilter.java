package com.techchallenge.usuario.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Le o header "Authorization: Bearer <token>", valida o JWT e, se valido,
 * autentica a requisicao no contexto de seguranca do Spring.
 *
 * Endpoints publicos (POST /v1/login e POST /v1/usuarios) sao liberados
 * direto no SecurityConfig, entao este filtro nem precisa saber quais rotas
 * sao publicas -- ele so tenta autenticar quando ha um header Authorization;
 * se nao houver (ou o token for invalido), a requisicao segue sem autenticacao
 * e quem decide se isso e' um problema e' a regra de authorizeHttpRequests.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String PREFIXO_BEARER = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith(PREFIXO_BEARER)) {
            String token = authHeader.substring(PREFIXO_BEARER.length());

            if (jwtService.tokenValido(token)) {
                String login = jwtService.extrairLogin(token);
                var authentication = new UsernamePasswordAuthenticationToken(
                        login, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
