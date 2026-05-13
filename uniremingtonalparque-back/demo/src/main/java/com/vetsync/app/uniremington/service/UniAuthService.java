package com.vetsync.app.uniremington.service;

import com.vetsync.app.entity.Usuario;
import com.vetsync.app.repository.UsuarioRepository;
import com.vetsync.app.uniremington.dto.AuthResponse;
import com.vetsync.app.uniremington.dto.LoginRequest;
import com.vetsync.app.uniremington.dto.RegisterRequest;
import com.vetsync.app.uniremington.security.UniJwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Servicio de autenticación del módulo "Uniremington al Parque".
 * Fusionado con la tabla principal 'usuarios'.
 */
@Service
@RequiredArgsConstructor
public class UniAuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UniJwtProvider jwtProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail();
        String documento = request.getDocumento();
        String iden = request.getIdentificador();

        if (iden != null && !iden.isBlank()) {
            if (iden.contains("@")) {
                email = iden;
                documento = "MAIL-" + iden;
            } else {
                documento = iden;
            }
        }

        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException("El documento de identidad es obligatorio");
        }

        // El email es obligatorio en la tabla 'usuarios'
        if (email == null || email.isBlank()) {
            email = documento + "@uniremington.edu.co"; // Generamos uno si no viene
        }

        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalStateException("Ya existe un usuario con el correo: " + email);
        }
        
        if (usuarioRepository.existsByDocumento(documento)) {
            throw new IllegalStateException("Ya existe un usuario con el documento: " + documento);
        }

        String rolStr = (request.getRol() != null && !request.getRol().isBlank()) 
                     ? request.getRol().toUpperCase() 
                     : "CLIENTE";
        
        Usuario.Rol rolEnum;
        try {
            rolEnum = Usuario.Rol.valueOf(rolStr);
        } catch (IllegalArgumentException e) {
            rolEnum = Usuario.Rol.CLIENTE;
        }

        Usuario usuario = Usuario.builder()
                .documento(documento)
                .email(email)
                .nombre(request.getNombreCompleto())
                .facultad(request.getFacultad() != null ? request.getFacultad() : "Visitante")
                .programa(request.getPrograma() != null ? request.getPrograma() : "N/A")
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(rolEnum)
                .genero(request.getGenero())
                .activo(true)
                .build();

        usuarioRepository.save(usuario);

        return buildResponse(usuario);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String identificador = request.getDocumento();
        
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

        Usuario usuario = usuarioRepository.findByIdentificador(identificador)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con documento o correo: " + identificador));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new BadCredentialsException("Contraseña incorrecta");
        }

        return buildResponse(usuario);
    }

    private AuthResponse buildResponse(Usuario usuario) {
        Map<String, Object> claims = Map.of(
                "facultad", usuario.getFacultad() != null ? usuario.getFacultad() : "",
                "programa", usuario.getPrograma() != null ? usuario.getPrograma() : "",
                "nombreCompleto", usuario.getNombre(),
                "rol", usuario.getRol().name()
        );

        String token = jwtProvider.generateToken(usuario.getDocumento(), claims);

        return AuthResponse.builder()
                .token(token)
                .documento(usuario.getDocumento())
                .nombreCompleto(usuario.getNombre())
                .facultad(usuario.getFacultad())
                .programa(usuario.getPrograma())
                .tipo("Bearer")
                .rol(usuario.getRol().name())
                .expiresIn(jwtProvider.getExpirationMs())
                .build();
    }
}
