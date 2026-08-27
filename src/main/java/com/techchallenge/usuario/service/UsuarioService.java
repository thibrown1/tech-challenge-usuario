package com.techchallenge.usuario.service;

import com.techchallenge.usuario.dto.EnderecoDTO;
import com.techchallenge.usuario.dto.TrocaSenhaDTO;
import com.techchallenge.usuario.dto.UsuarioAtualizacaoDTO;
import com.techchallenge.usuario.dto.UsuarioCadastroDTO;
import com.techchallenge.usuario.dto.UsuarioResponseDTO;
import com.techchallenge.usuario.entity.Endereco;
import com.techchallenge.usuario.entity.Usuario;
import com.techchallenge.usuario.exception.EmailJaCadastradoException;
import com.techchallenge.usuario.exception.SenhaInvalidaException;
import com.techchallenge.usuario.exception.UsuarioNaoEncontradoException;
import com.techchallenge.usuario.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Camada de regra de negocio do dominio Usuario.
 *
 * O Controller nunca fala diretamente com o Repository -- tudo passa por
 * aqui. E' aqui que moram as regras que nao cabem numa simples anotacao
 * de validacao, como "email precisa ser unico no sistema" ou "as duas
 * senhas digitadas precisam ser iguais".
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public UsuarioResponseDTO cadastrar(UsuarioCadastroDTO dto) {
        // Regra de negocio: email unico. Isso fica aqui, na camada de
        // servico, e nao so como constraint do banco, porque assim
        // conseguimos devolver um erro de negocio claro (409 Conflict)
        // em vez de deixar a aplicacao estourar uma excecao generica de
        // banco de dados.
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new EmailJaCadastradoException(dto.email());
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setLogin(dto.login());
        usuario.setTipo(dto.tipo());
        usuario.setEndereco(paraEndereco(dto.endereco()));
        usuario.setSenhaHash(passwordEncoder.encode(dto.senha()));
        // dataUltimaAlteracao e' preenchida sozinha pelo @PrePersist da entidade

        Usuario salvo = usuarioRepository.save(usuario);
        return UsuarioResponseDTO.from(salvo);
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioAtualizacaoDTO dto) {
        Usuario usuario = buscarOuLancar(id);

        // Se o email mudou, precisa validar unicidade de novo -- senao
        // um usuario poderia "roubar" o email de outro na atualizacao.
        if (!usuario.getEmail().equalsIgnoreCase(dto.email())
                && usuarioRepository.existsByEmail(dto.email())) {
            throw new EmailJaCadastradoException(dto.email());
        }

        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setLogin(dto.login());
        usuario.setTipo(dto.tipo());
        usuario.setEndereco(paraEndereco(dto.endereco()));
        // senha propositalmente NAO e' tocada aqui -- ver TrocaSenhaDTO
        // dataUltimaAlteracao e' atualizada sozinha pelo @PreUpdate da entidade

        Usuario atualizado = usuarioRepository.save(usuario);
        return UsuarioResponseDTO.from(atualizado);
    }

    @Transactional
    public void trocarSenha(Long id, TrocaSenhaDTO dto) {
        Usuario usuario = buscarOuLancar(id);

        if (!dto.novaSenha().equals(dto.confirmacaoSenha())) {
            throw new SenhaInvalidaException("novaSenha e confirmacaoSenha nao sao iguais.");
        }

        usuario.setSenhaHash(passwordEncoder.encode(dto.novaSenha()));
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void excluir(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new UsuarioNaoEncontradoException(id);
        }
        usuarioRepository.deleteById(id);
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        return UsuarioResponseDTO.from(buscarOuLancar(id));
    }

    /**
     * Busca por nome usando LIKE/contains (nao igualdade exata) --
     * conforme especificado no enunciado: GET /usuarios?nome=Joao deve
     * encontrar "Joao Silva", "Joao Pedro" etc, nao so um nome identico.
     */
    public List<UsuarioResponseDTO> buscarPorNome(String nome) {
        return usuarioRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(UsuarioResponseDTO::from)
                .toList();
    }

    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponseDTO::from)
                .toList();
    }

    private Usuario buscarOuLancar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
    }

    private Endereco paraEndereco(EnderecoDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Endereco(dto.rua(), dto.numero(), dto.cidade(), dto.estado(), dto.cep());
    }
}
