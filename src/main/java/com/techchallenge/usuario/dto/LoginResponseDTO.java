package com.techchallenge.usuario.dto;

/**
 * Resposta do login. Agora que Spring Security + JWT foi implementado
 * (desafio extra), o campo "token" traz o Bearer token que deve ser enviado
 * no header Authorization das demais chamadas protegidas da API.
 */
public record LoginResponseDTO(
        boolean autenticado,
        Long usuarioId,
        String nome,
        String mensagem,
        String token
) {
}
