package com.techchallenge.usuario.entity;

/**
 * Define os dois papeis que um usuario pode assumir no sistema.
 * Optamos por um campo discriminador (enum) em vez de heranca de classes
 * (Cliente extends Usuario / DonoRestaurante extends Usuario) porque, nesta
 * fase, os dois tipos nao possuem comportamento ou atributos proprios --
 * a diferenca entre eles e puramente de papel. Isso evita tabelas e joins
 * desnecessarios. Se em fases futuras cada tipo passar a acumular atributos
 * proprios (ex: DonoRestaurante ganhar uma lista de restaurantes), essa
 * decisao pode ser revisitada.
 */
public enum TipoUsuario {
    CLIENTE,
    DONO_RESTAURANTE
}
