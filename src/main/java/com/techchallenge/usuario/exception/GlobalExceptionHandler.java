package com.techchallenge.usuario.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Centraliza o tratamento de erro de TODA a API num unico lugar, usando
 * ProblemDetail (RFC 7807), suportado nativamente pelo Spring Boot 3.
 *
 * Sem isso, cada Controller teria que tratar seus proprios erros e cada
 * um provavelmente devolveria um formato de JSON diferente. Com isso,
 * toda resposta de erro da API -- email duplicado, usuario nao encontrado,
 * campo invalido, login errado -- sai no MESMO formato, o que facilita
 * tanto os testes automatizados quanto qualquer frontend consumindo a API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ProblemDetail handleEmailJaCadastrado(EmailJaCadastradoException ex) {
        return construirProblemDetail(HttpStatus.CONFLICT, "Email ja cadastrado", ex.getMessage());
    }

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ProblemDetail handleUsuarioNaoEncontrado(UsuarioNaoEncontradoException ex) {
        return construirProblemDetail(HttpStatus.NOT_FOUND, "Usuario nao encontrado", ex.getMessage());
    }

    @ExceptionHandler(SenhaInvalidaException.class)
    public ProblemDetail handleSenhaInvalida(SenhaInvalidaException ex) {
        return construirProblemDetail(HttpStatus.BAD_REQUEST, "Senha invalida", ex.getMessage());
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ProblemDetail handleCredenciaisInvalidas(CredenciaisInvalidasException ex) {
        return construirProblemDetail(HttpStatus.UNAUTHORIZED, "Falha na autenticacao", ex.getMessage());
    }

    /**
     * Captura os erros de validacao do Bean Validation (@NotBlank, @Email
     * etc, definidos nos DTOs). Junta todos os campos invalidos numa unica
     * resposta, em vez de o cliente descobrir os erros um de cada vez.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();
        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            erros.put(erro.getField(), erro.getDefaultMessage());
        }

        ProblemDetail pd = construirProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Erro de validacao",
                "Um ou mais campos estao invalidos."
        );
        pd.setProperty("campos", erros);
        return pd;
    }

    private ProblemDetail construirProblemDetail(HttpStatus status, String titulo, String detalhe) {
        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setTitle(titulo);
        pd.setDetail(detalhe);
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }
}
