package com.techchallenge.usuario.service;

import com.techchallenge.usuario.dto.LoginDTO;
import com.techchallenge.usuario.dto.LoginResponseDTO;
import com.techchallenge.usuario.entity.Usuario;
import com.techchallenge.usuario.exception.CredenciaisInvalidasException;
import com.techchallenge.usuario.repository.UsuarioRepository;
import com.techchallenge.usuario.security.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Autenticacao simples (login/senha consultados no banco, sem depender do
 * AuthenticationManager do Spring Security -- conforme o enunciado, que
 * torna Spring Security opcional para ESSA parte). A partir da autenticacao
 * bem-sucedida, agora tambem emitimos um JWT (desafio extra), que e' o que
 * o Spring Security de fato usa para proteger as demais rotas da API.
 */
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UsuarioRepository usuarioRepository, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
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

        String token = jwtService.gerarToken(usuario.getId(), usuario.getLogin(), usuario.getTipo().name());

        return new LoginResponseDTO(true, usuario.getId(), usuario.getNome(),
                "Login realizado com sucesso.", token);
    }
}
