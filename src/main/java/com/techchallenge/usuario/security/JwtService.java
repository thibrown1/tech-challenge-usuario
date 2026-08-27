package com.techchallenge.usuario.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Geracao e validacao de tokens JWT (desafio extra do enunciado: autenticacao
 * com Spring Security + JWT).
 *
 * O token carrega o login do usuario como subject, mais o id e o tipo como
 * claims extras, para que o filtro de autenticacao nao precise consultar o
 * banco a cada requisicao protegida.
 */
@Service
public class JwtService {

    private final SecretKey chave;
    private final long expiracaoMs;

    public JwtService(
            @Value("${jwt.secret}") String segredo,
            @Value("${jwt.expiration-ms}") long expiracaoMs) {
        this.chave = Keys.hmacShaKeyFor(segredo.getBytes());
        this.expiracaoMs = expiracaoMs;
    }

    public String gerarToken(Long usuarioId, String login, String tipo) {
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + expiracaoMs);

        return Jwts.builder()
                .subject(login)
                .claims(Map.of("usuarioId", usuarioId, "tipo", tipo))
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(chave)
                .compact();
    }

    public String extrairLogin(String token) {
        return extrairClaim(token, Claims::getSubject);
    }

    /**
     * Valida assinatura e expiracao do token. Qualquer token malformado,
     * com assinatura invalida ou expirado resulta em "false" -- o chamador
     * (JwtAuthenticationFilter) simplesmente nao autentica a requisicao,
     * deixando o Spring Security barrar o acesso com 401.
     */
    public boolean tokenValido(String token) {
        try {
            return !extrairExpiracao(token).before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Date extrairExpiracao(String token) {
        return extrairClaim(token, Claims::getExpiration);
    }

    private <T> T extrairClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return resolver.apply(claims);
    }
}
