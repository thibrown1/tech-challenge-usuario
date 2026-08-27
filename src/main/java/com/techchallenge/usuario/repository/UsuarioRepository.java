package com.techchallenge.usuario.repository;

import com.techchallenge.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * O Spring Data JPA gera a implementacao desta interface automaticamente
 * em tempo de execucao -- nao escrevemos nenhum SQL nem implementacao aqui.
 *
 * JpaRepository<Usuario, Long> ja da de graca: save, findById, findAll,
 * deleteById, etc. Os metodos abaixo sao "query methods": o Spring le o
 * NOME do metodo e monta a query sozinho.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca por nome usando LIKE (contains), nao igualdade exata.
     * O sufixo "IgnoreCase" torna a busca case-insensitive, o que e'
     * o comportamento esperado numa busca por nome.
     */
    List<Usuario> findByNomeContainingIgnoreCase(String nome);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByLogin(String login);

    boolean existsByEmail(String email);
}
