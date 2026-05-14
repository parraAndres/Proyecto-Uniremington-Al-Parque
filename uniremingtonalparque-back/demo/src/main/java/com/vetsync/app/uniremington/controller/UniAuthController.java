package com.vetsync.app.uniremington.controller;

import com.vetsync.app.uniremington.dto.AuthResponse;
import com.vetsync.app.uniremington.dto.LoginRequest;
import com.vetsync.app.uniremington.dto.RegisterRequest;
import com.vetsync.app.uniremington.dto.ResetPasswordRequest;
import com.vetsync.app.uniremington.service.UniAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador de autenticación para el módulo "Uniremington al Parque".
 *
 * Rutas públicas (sin JWT requerido):
 *   POST /api/auth/register  → Registro de nuevo usuario con documento
 *   POST /api/auth/login     → Login por documento + contraseña → JWT
 */
@RestController
@RequestMapping("/uni/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Auth - Uniremington", description = "Registro y login para la PWA Uniremington al Parque")
public class UniAuthController {

    private final UniAuthService authService;

    /**
     * Registra un nuevo usuario del módulo social.
     * No requiere autenticación previa.
     */
    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Crea un usuario Uniremington con documento y contraseña cifrada BCrypt")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login por documento y contraseña.
     * Retorna JWT con documento y facultad en el payload.
     */
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Retorna un token JWT con documento y facultad para uso en el frontend")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Solicitar recuperación", description = "Envía un correo con un enlace de recuperación")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestParam String identificador) {
        authService.forgotPassword(identificador);
        return ResponseEntity.ok(Map.of("message", "Se ha enviado un enlace de recuperación a tu correo electrónico registrado."));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Restablecer contraseña", description = "Cambia la contraseña usando un token válido")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Tu contraseña ha sido restablecida exitosamente."));
    }
}
