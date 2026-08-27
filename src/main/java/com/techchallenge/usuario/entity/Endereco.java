package com.techchallenge.usuario.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Objeto de valor (Value Object) que representa o endereco de um usuario.
 *
 * E' um @Embeddable, ou seja, nao vira uma tabela propria no banco: seus
 * campos sao "achatados" dentro da propria tabela usuario (endereco_rua,
 * endereco_cidade etc). Faz sentido modelar assim porque um Endereco nao
 * tem identidade propria nem ciclo de vida independente do Usuario -- ele
 * so existe como um atributo composto do usuario a quem pertence.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {

    private String rua;
    private String numero;
    private String cidade;
    private String estado;
    private String cep;
}
