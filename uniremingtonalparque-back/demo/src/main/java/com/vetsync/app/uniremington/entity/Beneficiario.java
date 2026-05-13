package com.vetsync.app.uniremington.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Beneficiario atendido en la jornada social "Uniremington al Parque".
 * El ID es un UUID generado en el frontend (crypto.randomUUID()), por lo que
 * NO se usa @GeneratedValue — el backend lo acepta tal como llega.
 */
@Entity
@Table(name = "uni_beneficiarios", indexes = {
        @Index(name = "idx_uni_ben_documento", columnList = "documento", unique = true),
        @Index(name = "idx_uni_ben_municipio",  columnList = "municipio")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Beneficiario {

    @Id
    @Column(name = "id", length = 36, updatable = false, nullable = false)
    private String id;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String nombre;

    @NotBlank
    @Column(nullable = false, length = 30, unique = true)
    private String documento;

    @Column
    private Integer edad;

    @Column(length = 20)
    private String genero;

    @Column(length = 30)
    private String telefono;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String municipio;

    @Column(length = 100)
    private String barrio;

    @Column(length = 100)
    private String vereda;

    @Column(length = 100)
    private String tipoPoblacion;

    @Column(length = 150)
    private String servicioSolicitado;

    @Column
    private Boolean autorizaDatos;

    /** Fecha de registro en formato ISO (puede venir como String del frontend) */
    @Column(length = 50)
    private String fechaRegistro;

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
