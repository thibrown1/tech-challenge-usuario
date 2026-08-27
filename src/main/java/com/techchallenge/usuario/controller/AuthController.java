package com.techchallenge.usuario.controller;

import com.techchallenge.usuario.dto.LoginDTO;
import com.techchallenge.usuario.dto.LoginResponseDTO;
import com.techchallenge.usuario.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Autentica um usuario por login e senha")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(
                            name = "Sucesso",
                            value = """
                                    {
                                      "autenticado": true,
                                      "usuarioId": 1,
                                      "nome": "Thiago Silva",
                                      "mensagem": "Login realizado com sucesso."
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Login ou senha invalidos",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(
                            name = "Falha",
                            value = """
                                    {
                                      "title": "Falha na autenticacao",
                                      "status": 401,
                                      "detail": "Usuario ou senha invalidos.",
                                      "timestamp": "2026-08-15T11:30:00Z"
                                    }
                                    """)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
        return ResponseEntity.ok(authService.autenticar(dto));
    }
}
