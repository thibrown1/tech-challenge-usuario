package com.techchallenge.usuario.dto;

import com.techchallenge.usuario.entity.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de ENTRADA usado exclusivamente no POST de cadastro.
 * E' o unico DTO de escrita que carrega senha, porque e' o unico momento
 * em que uma senha inicial precisa ser definida.
 */
public record UsuarioCadastroDTO(

        @NotBlank(message = "nome e obrigatorio")
        String nome,

        @NotBlank(message = "email e obrigatorio")
        @Email(message = "email invalido")
        String email,

        @NotBlank(message = "login e obrigatorio")
        String login,

        EnderecoDTO endereco,

        @NotNull(message = "tipo e obrigatorio (CLIENTE ou DONO_RESTAURANTE)")
        TipoUsuario tipo,

        @NotBlank(message = "senha e obrigatoria")
        @Size(min = 6, message = "senha deve ter ao menos 6 caracteres")
        String senha
) {
}
