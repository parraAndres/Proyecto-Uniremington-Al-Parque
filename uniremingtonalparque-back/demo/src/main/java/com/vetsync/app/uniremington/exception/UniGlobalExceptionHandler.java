package com.vetsync.app.uniremington.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para el módulo Uniremington al Parque.
 * Retorna respuestas JSON estandarizadas con código HTTP apropiado.
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.vetsync.app.uniremington")
public class UniGlobalExceptionHandler {

    // ── 400 - Validación ─────────────────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(
                ErrorResponse.of(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                        "Error de validación en los campos", fieldErrors));
    }

    // ── 400 - Estado ilegal (documento duplicado, etc.) ──────────────

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.badRequest().body(
                ErrorResponse.of(HttpStatus.BAD_REQUEST, "CONFLICT", ex.getMessage(), null));
    }

    // ── 401 - Credenciales inválidas ─────────────────────────────────

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.of(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS",
                        "Documento o contraseña incorrectos", null));
    }

    // ── 404 - Usuario no encontrado ──────────────────────────────────

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.of(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", ex.getMessage(), null));
    }

    // ── 500 - Error genérico ─────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("[Uniremington] Error inesperado: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body(
                ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                        "Error interno del servidor", null));
    }

    // ── Modelo de respuesta de error ─────────────────────────────────

    public record ErrorResponse(
            int status,
            String error,
            String message,
            Map<String, String> details,
            LocalDateTime timestamp
    ) {
        static ErrorResponse of(HttpStatus status, String error, String message,
                                Map<String, String> details) {
            return new ErrorResponse(status.value(), error, message, details, LocalDateTime.now());
        }
    }
}
