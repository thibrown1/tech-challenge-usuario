package com.techchallenge.usuario.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Agregado raiz do dominio de usuarios.
 *
 * Representa tanto um Cliente quanto um Dono de Restaurante -- o campo
 * "tipo" e' quem diferencia o papel. A senha nunca e' exposta fora desta
 * classe (nao existe getter publico "normal" para ela em DTOs de resposta),
 * e e' sempre armazenada como hash (BCrypt), nunca em texto puro.
 */
@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String login;

    /**
     * Hash da senha (BCrypt), nunca a senha em texto puro.
     * O nome do campo e' propositalmente "senhaHash" para deixar isso
     * explicito em qualquer lugar do codigo que o referencie.
     */
    @Column(nullable = false)
    private String senhaHash;

    @Embedded
    private Endereco endereco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUsuario tipo;

    @Column(nullable = false)
    private LocalDateTime dataUltimaAlteracao;

    /**
     * Callback do JPA disparado automaticamente ANTES do INSERT.
     * Garante que a data de auditoria seja preenchida na criacao,
     * sem depender de o Service lembrar de fazer isso manualmente.
     */
    @PrePersist
    protected void aoCriar() {
        this.dataUltimaAlteracao = LocalDateTime.now();
    }

    /**
     * Callback do JPA disparado automaticamente ANTES do UPDATE.
     * Garante que toda alteracao atualize a data de auditoria.
     */
    @PreUpdate
    protected void aoAtualizar() {
        this.dataUltimaAlteracao = LocalDateTime.now();
    }
}
