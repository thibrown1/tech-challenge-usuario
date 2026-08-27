package com.techchallenge.usuario.controller;

import com.techchallenge.usuario.dto.TrocaSenhaDTO;
import com.techchallenge.usuario.dto.UsuarioAtualizacaoDTO;
import com.techchallenge.usuario.dto.UsuarioCadastroDTO;
import com.techchallenge.usuario.dto.UsuarioResponseDTO;
import com.techchallenge.usuario.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Endpoints de CRUD de usuario + troca de senha.
 * Prefixo /v1 = versionamento de API definido desde o primeiro endpoint,
 * conforme recomendado no enunciado.
 *
 * Cada rota tem, no Swagger, ao menos um exemplo de sucesso e um de falha,
 * conforme exigido no passo 6 do desafio.
 */
@RestController
@RequestMapping("/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Cadastra um novo usuario (Cliente ou Dono de Restaurante)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario criado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(
                            name = "Sucesso",
                            value = """
                                    {
                                      "id": 1,
                                      "nome": "Thiago Silva",
                                      "email": "thiago@teste.com",
                                      "login": "thiago.silva",
                                      "endereco": {
                                        "rua": "Rua Teste",
                                        "numero": "123",
                                        "cidade": "Sao Bernardo do Campo",
                                        "estado": "SP",
                                        "cep": "09600-000"
                                      },
                                      "tipo": "CLIENTE",
                                      "dataUltimaAlteracao": "2026-08-15T10:30:00"
                                    }
                                    """))),
            @ApiResponse(responseCode = "409", description = "E-mail ja cadastrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(
                            name = "Email duplicado",
                            value = """
                                    {
                                      "title": "Email ja cadastrado",
                                      "status": 409,
                                      "detail": "O e-mail 'thiago@teste.com' ja esta cadastrado.",
                                      "timestamp": "2026-08-15T10:31:00Z"
                                    }
                                    """)))
    })
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@Valid @RequestBody UsuarioCadastroDTO dto) {
        UsuarioResponseDTO criado = usuarioService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @Operation(summary = "Atualiza os dados de um usuario (exceto a senha)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario atualizado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(
                            name = "Sucesso",
                            value = """
                                    {
                                      "id": 1,
                                      "nome": "Thiago Silva Santos",
                                      "email": "thiago@teste.com",
                                      "login": "thiago.silva",
                                      "endereco": {
                                        "rua": "Rua Nova",
                                        "numero": "456",
                                        "cidade": "Santo Andre",
                                        "estado": "SP",
                                        "cep": "09000-000"
                                      },
                                      "tipo": "CLIENTE",
                                      "dataUltimaAlteracao": "2026-08-15T11:00:00"
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Usuario nao encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(
                            name = "Nao encontrado",
                            value = """
                                    {
                                      "title": "Usuario nao encontrado",
                                      "status": 404,
                                      "detail": "Usuario com id 999 nao foi encontrado.",
                                      "timestamp": "2026-08-15T11:01:00Z"
                                    }
                                    """)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioAtualizacaoDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizar(id, dto));
    }

    @Operation(summary = "Exclui um usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario excluido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuario nao encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(
                            name = "Nao encontrado",
                            value = """
                                    {
                                      "title": "Usuario nao encontrado",
                                      "status": 404,
                                      "detail": "Usuario com id 999 nao foi encontrado.",
                                      "timestamp": "2026-08-15T11:05:00Z"
                                    }
                                    """)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Busca um usuario por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(
                            name = "Sucesso",
                            value = """
                                    {
                                      "id": 1,
                                      "nome": "Thiago Silva",
                                      "email": "thiago@teste.com",
                                      "login": "thiago.silva",
                                      "endereco": null,
                                      "tipo": "CLIENTE",
                                      "dataUltimaAlteracao": "2026-08-15T10:30:00"
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Usuario nao encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(
                            name = "Nao encontrado",
                            value = """
                                    {
                                      "title": "Usuario nao encontrado",
                                      "status": 404,
                                      "detail": "Usuario com id 999 nao foi encontrado.",
                                      "timestamp": "2026-08-15T11:10:00Z"
                                    }
                                    """)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @Operation(summary = "Lista usuarios, opcionalmente filtrando por nome (busca parcial)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuarios (pode vir vazia)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(
                            name = "Sucesso - encontrado",
                            value = """
                                    [
                                      {
                                        "id": 1,
                                        "nome": "Thiago Silva",
                                        "email": "thiago@teste.com",
                                        "login": "thiago.silva",
                                        "endereco": null,
                                        "tipo": "CLIENTE",
                                        "dataUltimaAlteracao": "2026-08-15T10:30:00"
                                      }
                                    ]
                                    """))),
            @ApiResponse(responseCode = "200", description = "Nenhum usuario encontrado para o filtro",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(
                            name = "Lista vazia",
                            value = "[]")))
    })
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> buscar(
            @RequestParam(required = false) String nome) {
        if (nome != null && !nome.isBlank()) {
            return ResponseEntity.ok(usuarioService.buscarPorNome(nome));
        }
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @Operation(summary = "Troca a senha de um usuario (endpoint dedicado, separado da atualizacao geral)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "novaSenha e confirmacaoSenha nao coincidem",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(
                            name = "Senhas divergentes",
                            value = """
                                    {
                                      "title": "Senha invalida",
                                      "status": 400,
                                      "detail": "novaSenha e confirmacaoSenha nao sao iguais.",
                                      "timestamp": "2026-08-15T11:20:00Z"
                                    }
                                    """)))
    })
    @PostMapping("/{id}/senha")
    public ResponseEntity<Void> trocarSenha(
            @PathVariable Long id,
            @Valid @RequestBody TrocaSenhaDTO dto) {
        usuarioService.trocarSenha(id, dto);
        return ResponseEntity.noContent().build();
    }
}
