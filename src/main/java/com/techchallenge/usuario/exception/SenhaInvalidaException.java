package com.techchallenge.usuario.exception;

public class SenhaInvalidaException extends RuntimeException {
    public SenhaInvalidaException(String motivo) {
        super(motivo);
    }
}
