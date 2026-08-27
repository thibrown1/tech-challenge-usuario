package com.techchallenge.usuario.exception;

public class EmailJaCadastradoException extends RuntimeException {
    public EmailJaCadastradoException(String email) {
        super("O e-mail '" + email + "' ja esta cadastrado.");
    }
}
