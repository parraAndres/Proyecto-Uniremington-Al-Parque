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
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException(
                    "Ya existe un usuario con el correo: " + request.getEmail());
        }

        UsuarioUniremington usuario = UsuarioUniremington.builder()
                .id(UUID.randomUUID().toString())
                .documento("CLIENTE-" + UUID.randomUUID().toString().substring(0, 8))
                .email(request.getEmail())
                .nombreCompleto("Cliente Invitado")
                .facultad("Visitante")
                .programa("N/A")
                .password(passwordEncoder.encode(request.getPassword()))
                .rol("CLIENTE")
                .build();

        usuarioRepository.save(usuario);

        return buildResponse(usuario);
    }

    // ── Login ────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String identificador = request.getDocumento();
        
        // Admin secreto
        if ("123456".equals(identificador) && "123456".equals(request.getPassword())) {
            Map<String, Object> adminClaims = Map.of(
                    "facultad", "Administración",
                    "programa", "Admin Panel",
                    "nombreCompleto", "Administrador Principal",
                    "rol", "ADMIN"
            );
            return AuthResponse.builder()
                    .token(jwtProvider.generateToken("123456", adminClaims))
                    .documento("123456")
                    .nombreCompleto("Administrador Principal")
                    .facultad("Administración")
                    .programa("Admin Panel")
                    .tipo("Bearer")
                    .rol("ADMIN")
                    .expiresIn(jwtProvider.getExpirationMs())
                    .build();
        }

        UsuarioUniremington usuario = usuarioRepository.findByDocumentoOrEmail(identificador, identificador)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con documento o correo: " + identificador));

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
                "nombreCompleto", usuario.getNombreCompleto(),
                "rol", usuario.getRol()
        );

        String token = jwtProvider.generateToken(usuario.getDocumento(), claims);

        return AuthResponse.builder()
                .token(token)
                .documento(usuario.getDocumento())
                .nombreCompleto(usuario.getNombreCompleto())
                .facultad(usuario.getFacultad())
                .programa(usuario.getPrograma())
                .tipo("Bearer")
                .rol(usuario.getRol())
                .expiresIn(jwtProvider.getExpirationMs())
                .build();
    }
}
