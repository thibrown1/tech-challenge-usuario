package com.techchallenge.usuario.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginDTO(

        @NotBlank(message = "usuario e obrigatorio")
        String usuario,

        @NotBlank(message = "senha e obrigatoria")
        String senha
) {
}
