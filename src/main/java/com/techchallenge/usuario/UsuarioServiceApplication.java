package com.techchallenge.usuario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplicacao.
 * O Spring Boot escaneia este pacote e todos os sub-pacotes procurando
 * classes anotadas (@Entity, @Repository, @Service, @RestController etc)
 * e monta tudo automaticamente.
 */
@SpringBootApplication
public class UsuarioServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsuarioServiceApplication.class, args);
    }
}
