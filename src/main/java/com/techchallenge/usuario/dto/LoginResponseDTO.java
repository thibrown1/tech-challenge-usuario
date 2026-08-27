package com.techchallenge.usuario.dto;

/**
 * Resposta simples de login. Como Spring Security e' opcional nesta fase
 * (conforme o enunciado), nao geramos um token JWT de verdade -- apenas
 * confirmamos a autenticacao e devolvemos os dados basicos do usuario.
 * Gerar um token JWT seria a evolucao natural disso numa fase futura.
 */
public record LoginResponseDTO(
        boolean autenticado,
        Long usuarioId,
        String nome,
        String mensagem
) {
}
