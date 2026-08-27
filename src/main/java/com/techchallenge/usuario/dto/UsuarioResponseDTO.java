package com.techchallenge.usuario.dto;

import com.techchallenge.usuario.entity.Endereco;
import com.techchallenge.usuario.entity.TipoUsuario;
import com.techchallenge.usuario.entity.Usuario;

import java.time.LocalDateTime;

/**
 * DTO de SAIDA. Note que nao existe campo de senha (nem hash) aqui --
 * essa e' a garantia de que a senha jamais e' devolvida pela API,
 * em nenhuma resposta, em nenhuma circunstancia.
 */
public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        String login,
        EnderecoDTO endereco,
        TipoUsuario tipo,
        LocalDateTime dataUltimaAlteracao
) {

    /**
     * Metodo de fabrica que converte a entidade (que tem a senha) no DTO
     * de resposta (que nunca tem). Toda saida da API passa por aqui.
     */
    public static UsuarioResponseDTO from(Usuario usuario) {
        Endereco end = usuario.getEndereco();
        EnderecoDTO enderecoDTO = end == null
                ? null
                : new EnderecoDTO(end.getRua(), end.getNumero(), end.getCidade(), end.getEstado(), end.getCep());

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getLogin(),
                enderecoDTO,
                usuario.getTipo(),
                usuario.getDataUltimaAlteracao()
        );
    }
}
