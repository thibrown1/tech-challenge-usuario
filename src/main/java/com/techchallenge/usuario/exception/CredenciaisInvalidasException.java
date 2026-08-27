package com.techchallenge.usuario.exception;

public class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException() {
        super("Usuario ou senha invalidos.");
    }
}
