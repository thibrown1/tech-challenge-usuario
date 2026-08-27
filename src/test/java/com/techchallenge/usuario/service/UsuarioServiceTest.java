package com.techchallenge.usuario.service;

import com.techchallenge.usuario.dto.EnderecoDTO;
import com.techchallenge.usuario.dto.TrocaSenhaDTO;
import com.techchallenge.usuario.dto.UsuarioAtualizacaoDTO;
import com.techchallenge.usuario.dto.UsuarioCadastroDTO;
import com.techchallenge.usuario.dto.UsuarioResponseDTO;
import com.techchallenge.usuario.entity.TipoUsuario;
import com.techchallenge.usuario.entity.Usuario;
import com.techchallenge.usuario.exception.EmailJaCadastradoException;
import com.techchallenge.usuario.exception.SenhaInvalidaException;
import com.techchallenge.usuario.exception.UsuarioNaoEncontradoException;
import com.techchallenge.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitarios da regra de negocio de UsuarioService (desafio extra:
 * JUnit + Mockito). O UsuarioRepository e' mockado -- nenhum destes testes
 * sobe contexto Spring nem toca um banco de dados de verdade, entao rodam
 * em milissegundos e cobrem exatamente as regras de negocio da camada de
 * servico (as mesmas exercitadas pela collection do Postman).
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioCadastroDTO cadastroValido;

    @BeforeEach
    void setUp() {
        cadastroValido = new UsuarioCadastroDTO(
                "Thiago Silva", "thiago@teste.com", "thiago.silva",
                new EnderecoDTO("Rua Teste", "123", "Sao Bernardo do Campo", "SP", "09600-000"),
                TipoUsuario.CLIENTE, "senha123");
    }

    @Test
    void deveCadastrarUsuarioComSucessoQuandoEmailNaoExiste() {
        when(usuarioRepository.existsByEmail(cadastroValido.email())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        UsuarioResponseDTO resultado = usuarioService.cadastrar(cadastroValido);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.email()).isEqualTo("thiago@teste.com");
        assertThat(resultado.tipo()).isEqualTo(TipoUsuario.CLIENTE);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoAoCadastrarComEmailJaExistente() {
        when(usuarioRepository.existsByEmail(cadastroValido.email())).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.cadastrar(cadastroValido))
                .isInstanceOf(EmailJaCadastradoException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveAtualizarUsuarioComSucesso() {
        Usuario existente = usuarioExistente();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioAtualizacaoDTO dto = new UsuarioAtualizacaoDTO(
                "Thiago Santos", "thiago@teste.com", "thiago.silva",
                new EnderecoDTO("Rua Nova", "456", "Santo Andre", "SP", "09000-000"),
                TipoUsuario.CLIENTE);

        UsuarioResponseDTO resultado = usuarioService.atualizar(1L, dto);

        assertThat(resultado.nome()).isEqualTo("Thiago Santos");
    }

    @Test
    void deveLancarExcecaoAoAtualizarUsuarioInexistente() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        UsuarioAtualizacaoDTO dto = new UsuarioAtualizacaoDTO(
                "X", "x@teste.com", "x.login", null, TipoUsuario.CLIENTE);

        assertThatThrownBy(() -> usuarioService.atualizar(999L, dto))
                .isInstanceOf(UsuarioNaoEncontradoException.class);
    }

    @Test
    void deveTrocarSenhaComSucessoQuandoConfirmacaoBate() {
        Usuario existente = usuarioExistente();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(existente);

        TrocaSenhaDTO dto = new TrocaSenhaDTO("novaSenha456", "novaSenha456");

        usuarioService.trocarSenha(1L, dto);

        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoAoTrocarSenhaComConfirmacaoDiferente() {
        Usuario existente = usuarioExistente();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));

        TrocaSenhaDTO dto = new TrocaSenhaDTO("senhaA", "senhaB");

        assertThatThrownBy(() -> usuarioService.trocarSenha(1L, dto))
                .isInstanceOf(SenhaInvalidaException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveExcluirUsuarioExistente() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        usuarioService.excluir(1L);

        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoExcluirUsuarioInexistente() {
        when(usuarioRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.excluir(999L))
                .isInstanceOf(UsuarioNaoEncontradoException.class);

        verify(usuarioRepository, never()).deleteById(any());
    }

    @Test
    void deveBuscarUsuariosPorNomeParcial() {
        when(usuarioRepository.findByNomeContainingIgnoreCase("thi"))
                .thenReturn(List.of(usuarioExistente()));

        List<UsuarioResponseDTO> resultado = usuarioService.buscarPorNome("thi");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nome()).isEqualTo("Thiago Silva");
    }

    private Usuario usuarioExistente() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Thiago Silva");
        usuario.setEmail("thiago@teste.com");
        usuario.setLogin("thiago.silva");
        usuario.setSenhaHash("hash-fake");
        usuario.setTipo(TipoUsuario.CLIENTE);
        usuario.setDataUltimaAlteracao(LocalDateTime.now());
        return usuario;
    }
}
