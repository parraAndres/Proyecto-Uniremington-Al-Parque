package com.vetsync.app.uniremington.service;

import com.vetsync.app.uniremington.dto.AuthResponse;
import com.vetsync.app.uniremington.dto.LoginRequest;
import com.vetsync.app.uniremington.dto.RegisterRequest;
import com.vetsync.app.uniremington.entity.Academico;
import com.vetsync.app.uniremington.entity.Beneficiario;
import com.vetsync.app.uniremington.entity.PasswordResetToken;
import com.vetsync.app.uniremington.entity.UsuarioUniremington;
import com.vetsync.app.uniremington.repository.AcademicoRepository;
import com.vetsync.app.uniremington.repository.BeneficiarioRepository;
import com.vetsync.app.uniremington.repository.PasswordResetTokenRepository;
import com.vetsync.app.uniremington.repository.UsuarioUniremingtonRepository;
import com.vetsync.app.uniremington.security.UniJwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UniAuthService {

    private final UsuarioUniremingtonRepository usuarioRepository;
    private final BeneficiarioRepository beneficiarioRepository;
    private final AcademicoRepository academicoRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final UniJwtProvider jwtProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail();
        String documento = request.getDocumento();

        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException("El documento de identidad es obligatorio");
        }

        if (email != null && !email.isBlank() && usuarioRepository.existsByEmail(email)) {
            throw new IllegalStateException("Ya existe un usuario con el correo: " + email);
        }
        
        if (usuarioRepository.existsByDocumento(documento)) {
            throw new IllegalStateException("Ya existe un usuario con el documento: " + documento);
        }

        // Lógica de roles automática
        String rolRaw = (request.getRol() != null) ? request.getRol().toLowerCase() : "cliente";
        String rolFinal;
        
        if (rolRaw.equals("cliente")) {
            rolFinal = "BENEFICIARIO";
        } else {
            rolFinal = rolRaw.toUpperCase(); // ADMIN, ESTUDIANTE, PROFESOR
        }

        UsuarioUniremington usuario = UsuarioUniremington.builder()
                .id(UUID.randomUUID().toString())
                .documento(documento)
                .email(email)
                .nombreCompleto(request.getNombreCompleto())
                .facultad(request.getFacultad() != null ? request.getFacultad() : "Visitante")
                .programa(request.getPrograma() != null ? request.getPrograma() : "N/A")
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(rolFinal)
                .genero(request.getGenero())
                .activo(true)
                .build();

        usuarioRepository.save(usuario);
        
        // Sincronización con tablas respectivas
        if ("BENEFICIARIO".equals(rolFinal)) {
            Beneficiario beneficiario = Beneficiario.builder()
                    .id(usuario.getId()) // Compartir el mismo ID
                    .documento(usuario.getDocumento())
                    .nombre(usuario.getNombreCompleto())
                    .genero(usuario.getGenero())
                    .municipio("Sede Central") // Valor por defecto inicial
                    .fechaRegistro(LocalDateTime.now().toString())
                    .build();
            beneficiarioRepository.save(beneficiario);
        } else if ("ESTUDIANTE".equals(rolFinal)) {
            Academico academico = Academico.builder()
                    .id(usuario.getId()) // Compartir el mismo ID
                    .estudianteId(usuario.getDocumento())
                    .nombreEstudiante(usuario.getNombreCompleto())
                    .facultad(usuario.getFacultad())
                    .programa(usuario.getPrograma())
                    .build();
            academicoRepository.save(academico);
        }

        return buildResponse(usuario);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String identificador = request.getDocumento();
        
        // Admin secreto
        if ("123456".equals(identificador) && "123456".equals(request.getPassword())) {
            return AuthResponse.builder()
                    .token(jwtProvider.generateToken("123456", Map.of("rol", "ADMIN", "facultad", "Administración")))
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

        if (!usuario.isActivo()) {
            throw new BadCredentialsException("Esta cuenta ha sido bloqueada. Contacte al administrador.");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new BadCredentialsException("Contraseña incorrecta");
        }

        return buildResponse(usuario);
    }

    @Transactional
    public void forgotPassword(String identificador) {
        UsuarioUniremington usuario = usuarioRepository.findByDocumentoOrEmail(identificador, identificador)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró una cuenta asociada a: " + identificador));

        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalStateException("El usuario no tiene un correo electrónico configurado para la recuperación.");
        }

        // Eliminar tokens anteriores
        tokenRepository.deleteByUsuario(usuario);

        // Crear nuevo token (válido por 1 hora)
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .usuario(usuario)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .build();

        tokenRepository.save(resetToken);

        // Enviar correo
        emailService.sendResetPasswordEmail(usuario.getEmail(), usuario.getNombreCompleto(), token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("El enlace de recuperación es inválido o ya ha sido utilizado."));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new IllegalStateException("El enlace de recuperación ha expirado.");
        }

        UsuarioUniremington usuario = resetToken.getUsuario();
        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);

        // Limpiar el token
        tokenRepository.delete(resetToken);
    }

    private AuthResponse buildResponse(UsuarioUniremington usuario) {
        Map<String, Object> claims = Map.of(
                "facultad", usuario.getFacultad(),
                "programa", usuario.getPrograma(),
                "nombreCompleto", usuario.getNombreCompleto(),
                "rol", usuario.getRol(),
                "activo", usuario.isActivo()
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
