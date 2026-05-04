package com.vetsync.app.uniremington.service;

import com.vetsync.app.uniremington.dto.AuthResponse;
import com.vetsync.app.uniremington.dto.LoginRequest;
import com.vetsync.app.uniremington.dto.RegisterRequest;
import com.vetsync.app.uniremington.entity.UsuarioUniremington;
import com.vetsync.app.uniremington.repository.UsuarioUniremingtonRepository;
import com.vetsync.app.uniremington.security.UniJwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

/**
 * Servicio de autenticación del módulo "Uniremington al Parque".
 *
 * Gestiona el registro de usuarios (con cifrado BCrypt) y el login
 * por documento + contraseña, retornando un JWT con facultad en el payload.
 */
@Service
@RequiredArgsConstructor
public class UniAuthService {

    private final UsuarioUniremingtonRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UniJwtProvider jwtProvider;

    // ── Registro ─────────────────────────────────────────────────────

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByDocumento(request.getDocumento())) {
            throw new IllegalStateException(
                    "Ya existe un usuario con el documento: " + request.getDocumento());
        }

        UsuarioUniremington usuario = UsuarioUniremington.builder()
                .id(UUID.randomUUID().toString())          // generado en el backend para registro
                .documento(request.getDocumento())
                .nombreCompleto(request.getNombreCompleto())
                .facultad(request.getFacultad())
                .programa(request.getPrograma())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        usuarioRepository.save(usuario);

        return buildResponse(usuario);
    }

    // ── Login ────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        UsuarioUniremington usuario = usuarioRepository.findByDocumento(request.getDocumento())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con documento: " + request.getDocumento()));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new BadCredentialsException("Contraseña incorrecta");
        }

        return buildResponse(usuario);
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private AuthResponse buildResponse(UsuarioUniremington usuario) {
        Map<String, Object> claims = Map.of(
                "facultad", usuario.getFacultad(),
                "programa", usuario.getPrograma(),
                "nombreCompleto", usuario.getNombreCompleto()
        );

        String token = jwtProvider.generateToken(usuario.getDocumento(), claims);

        return AuthResponse.builder()
                .token(token)
                .documento(usuario.getDocumento())
                .nombreCompleto(usuario.getNombreCompleto())
                .facultad(usuario.getFacultad())
                .programa(usuario.getPrograma())
                .tipo("Bearer")
                .expiresIn(jwtProvider.getExpirationMs())
                .build();
    }
}
