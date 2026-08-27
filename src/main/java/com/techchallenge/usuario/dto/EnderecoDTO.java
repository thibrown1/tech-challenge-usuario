package com.techchallenge.usuario.dto;

public record EnderecoDTO(
        String rua,
        String numero,
        String cidade,
        String estado,
        String cep
) {
}
