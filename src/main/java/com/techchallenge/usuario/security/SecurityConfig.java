package com.techchallenge.usuario.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.Instant;

/**
 * Configuracao de seguranca (desafio extra: Spring Security + JWT).
 *
 * Login (POST /v1/login) e cadastro (POST /v1/usuarios) continuam publicos --
 * e' preciso poder se cadastrar e logar sem ja possuir um token. Todas as
 * demais rotas de /v1/usuarios exigem um Bearer token valido, emitido pelo
 * proprio POST /v1/login.
 *
 * Respostas de erro de autenticacao/autorizacao (401/403) sao escritas no
 * mesmo formato ProblemDetail (RFC 7807) usado pelo GlobalExceptionHandler,
 * para manter toda a API consistente -- inclusive os erros que o Spring
 * Security gera antes mesmo de chegar num Controller.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, ObjectMapper objectMapper) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/v1/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/usuarios").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**", "/swagger-ui.html",
                                "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint((request, response, ex) -> escreverProblemDetail(
                                response, HttpStatus.UNAUTHORIZED, "Nao autenticado",
                                "E' necessario um Bearer token valido (obtido via POST /v1/login) "
                                        + "para acessar este recurso."))
                        .accessDeniedHandler((request, response, ex) -> escreverProblemDetail(
                                response, HttpStatus.FORBIDDEN, "Acesso negado",
                                "Voce nao tem permissao para acessar este recurso.")))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void escreverProblemDetail(
            HttpServletResponse response, HttpStatus status, String titulo, String detalhe) throws IOException {

        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setTitle(titulo);
        pd.setDetail(detalhe);
        pd.setProperty("timestamp", Instant.now());

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(pd));
    }
}
