package com.vetsync.app.uniremington.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Participación académica de un estudiante en la jornada social.
 * El ID es un UUID generado en el frontend (crypto.randomUUID()).
 */
@Entity
@Table(name = "uni_academico", indexes = {
        @Index(name = "idx_uni_acad_facultad_fecha", columnList = "facultad,fechaActividad"),
        @Index(name = "idx_uni_acad_programa",       columnList = "programa")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Academico {

    @Id
    @Column(name = "id", length = 36, updatable = false, nullable = false)
    private String id;

    @Column(length = 60)
    private String estudianteId;

    @Column(length = 150)
    private String nombreEstudiante;

    @Column(length = 120)
    private String programa;

    @Column(length = 80)
    private String facultad;

    @Column(precision = 8, scale = 2)
    private BigDecimal horasReportadas;

    @Column(length = 50)
    private String fechaActividad;

    @Column(length = 80)
    private String tipoParticipacion;

    /** Datos adicionales en JSON string */
    @Column(columnDefinition = "TEXT")
    private String datosExtra;

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
