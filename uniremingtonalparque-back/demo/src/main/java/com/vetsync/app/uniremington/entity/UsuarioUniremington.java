package com.vetsync.app.uniremington.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Usuario del módulo social "Uniremington al Parque".
 * Identificado por su número de documento (cédula/tarjeta), NO por email.
 * El id es un UUID generado en el frontend y enviado como String.
 */
@Entity
@Table(name = "uni_usuarios", indexes = {
        @Index(name = "idx_uni_usuario_documento", columnList = "documento", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioUniremington {

    /** UUID generado en el frontend con crypto.randomUUID() */
    @Id
    @Column(name = "id", length = 36, updatable = false, nullable = false)
    private String id;

    @Column(length = 150, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false, length = 30, unique = true)
    private String documento;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String nombreCompleto;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String facultad;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String programa;

    /** Contraseña cifrada con BCrypt */
    @NotBlank
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String rol; // ADMIN, ESTUDIANTE, CLIENTE

    @Column(length = 50)
    private String genero;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
