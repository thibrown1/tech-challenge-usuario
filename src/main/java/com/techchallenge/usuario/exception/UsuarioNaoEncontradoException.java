package com.techchallenge.usuario.exception;

public class UsuarioNaoEncontradoException extends RuntimeException {
    public UsuarioNaoEncontradoException(Long id) {
        super("Usuario com id " + id + " nao foi encontrado.");
    }
}
