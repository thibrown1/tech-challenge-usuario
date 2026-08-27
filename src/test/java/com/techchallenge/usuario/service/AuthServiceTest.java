package com.techchallenge.usuario.service;

import com.techchallenge.usuario.dto.LoginDTO;
import com.techchallenge.usuario.dto.LoginResponseDTO;
import com.techchallenge.usuario.entity.TipoUsuario;
import com.techchallenge.usuario.entity.Usuario;
import com.techchallenge.usuario.exception.CredenciaisInvalidasException;
import com.techchallenge.usuario.repository.UsuarioRepository;
import com.techchallenge.usuario.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Testes unitarios de AuthService (desafio extra: JUnit + Mockito).
 * JwtService tambem e' mockado aqui -- o objetivo destes testes e' validar
 * a regra de autenticacao (login/senha), nao a geracao do token em si
 * (isso e' responsabilidade do JwtService, que teria seus proprios testes
 * se o escopo do desafio pedisse).
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void deveAutenticarComSucessoQuandoLoginESenhaConferem() {
        Usuario usuario = usuarioComSenha("senha123");
        when(usuarioRepository.findByLogin("thiago.silva")).thenReturn(Optional.of(usuario));
        when(jwtService.gerarToken(1L, "thiago.silva", "CLIENTE")).thenReturn("token-fake-jwt");

        LoginResponseDTO resultado = authService.autenticar(new LoginDTO("thiago.silva", "senha123"));

        assertThat(resultado.autenticado()).isTrue();
        assertThat(resultado.token()).isEqualTo("token-fake-jwt");
        assertThat(resultado.usuarioId()).isEqualTo(1L);
    }

    @Test
    void deveLancarExcecaoQuandoLoginNaoExiste() {
        when(usuarioRepository.findByLogin("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.autenticar(new LoginDTO("inexistente", "qualquer")))
                .isInstanceOf(CredenciaisInvalidasException.class);
    }

    @Test
    void deveLancarExcecaoQuandoSenhaEstaErrada() {
        Usuario usuario = usuarioComSenha("senhaCorreta");
        when(usuarioRepository.findByLogin("thiago.silva")).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> authService.autenticar(new LoginDTO("thiago.silva", "senhaErrada")))
                .isInstanceOf(CredenciaisInvalidasException.class);
    }

    private Usuario usuarioComSenha(String senhaEmTextoPuro) {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Thiago Silva");
        usuario.setLogin("thiago.silva");
        usuario.setTipo(TipoUsuario.CLIENTE);
        usuario.setSenhaHash(new BCryptPasswordEncoder().encode(senhaEmTextoPuro));
        return usuario;
    }
}
