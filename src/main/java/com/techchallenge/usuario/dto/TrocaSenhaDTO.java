package com.techchallenge.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO do endpoint dedicado de troca de senha.
 * novaSenha e confirmacaoSenha precisam bater -- essa validacao acontece
 * no Service, porque envolve comparar dois campos entre si (nao da pra
 * fazer isso so com anotacao no proprio campo).
 */
public record TrocaSenhaDTO(

        @NotBlank(message = "novaSenha e obrigatoria")
        @Size(min = 6, message = "senha deve ter ao menos 6 caracteres")
        String novaSenha,

        @NotBlank(message = "confirmacaoSenha e obrigatoria")
        String confirmacaoSenha
) {
}
