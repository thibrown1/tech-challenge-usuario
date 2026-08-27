package com.techchallenge.usuario.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

/**
 * Declara o esquema de autenticacao Bearer/JWT para o Swagger UI (desafio
 * extra: Spring Security + JWT), habilitando o botao "Authorize" na
 * documentacao interativa para testar as rotas protegidas.
 */
@OpenAPIDefinition(info = @Info(
        title = "ConectaMesa - Usuario Service",
        version = "1.0.0",
        description = "API de gestao de usuarios (Clientes e Donos de Restaurante) do ConectaMesa."))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT")
public class OpenApiConfig {
}
