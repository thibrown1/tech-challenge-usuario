package com.techchallenge.usuario.service;

import com.techchallenge.usuario.dto.LoginDTO;
import com.techchallenge.usuario.dto.LoginResponseDTO;
import com.techchallenge.usuario.entity.Usuario;
import com.techchallenge.usuario.exception.CredenciaisInvalidasException;
import com.techchallenge.usuario.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Autenticacao simples, sem Spring Security (opcional nesta fase,
 * conforme o enunciado). Busca o usuario por login e compara a senha
 * enviada com o hash salvo no banco usando BCrypt.
 */
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public LoginResponseDTO autenticar(LoginDTO dto) {
        Usuario usuario = usuarioRepository.findByLogin(dto.usuario())
                .orElseThrow(CredenciaisInvalidasException::new);

        // matches() compara a senha em texto puro enviada com o hash
        // salvo, sem nunca precisar "descriptografar" o hash (BCrypt e'
        // uma via de mao unica -- e' assim que deve ser).
        if (!passwordEncoder.matches(dto.senha(), usuario.getSenhaHash())) {
            throw new CredenciaisInvalidasException();
        }

        return new LoginResponseDTO(true, usuario.getId(), usuario.getNome(), "Login realizado com sucesso.");
    }
}
