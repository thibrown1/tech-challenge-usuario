package com.techchallenge.usuario.dto;

import com.techchallenge.usuario.entity.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de ENTRADA usado no PUT de atualizacao.
 * Propositalmente SEM campo de senha: a troca de senha tem seu proprio
 * endpoint (POST /usuarios/{id}/senha) com suas proprias regras de
 * validacao (confirmacao de senha). Ver UsuarioService para a explicacao
 * completa dessa separacao de responsabilidade.
 */
public record UsuarioAtualizacaoDTO(

        @NotBlank(message = "nome e obrigatorio")
        String nome,

        @NotBlank(message = "email e obrigatorio")
        @Email(message = "email invalido")
        String email,

        @NotBlank(message = "login e obrigatorio")
        String login,

        EnderecoDTO endereco,

        @NotNull(message = "tipo e obrigatorio (CLIENTE ou DONO_RESTAURANTE)")
        TipoUsuario tipo
) {
}
