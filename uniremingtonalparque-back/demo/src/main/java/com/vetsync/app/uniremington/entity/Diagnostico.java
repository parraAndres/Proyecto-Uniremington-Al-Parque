package com.vetsync.app.uniremington.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Diagnóstico realizado a un beneficiario durante la jornada social.
 * El ID es un UUID generado en el frontend (crypto.randomUUID()).
 * Los datos del diagnóstico se almacenan como JSON string para máxima
 * flexibilidad ante la variedad de tipos de diagnóstico.
 */
@Entity
@Table(name = "uni_diagnosticos", indexes = {
        @Index(name = "idx_uni_diag_beneficiario", columnList = "beneficiarioId"),
        @Index(name = "idx_uni_diag_tipo_fecha",   columnList = "tipo,fechaDiagnostico")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diagnostico {

    @Id
    @Column(name = "id", length = 36, updatable = false, nullable = false)
    private String id;

    @Column(nullable = false, length = 36)
    private String beneficiarioId;

    @Column(length = 100)
    private String tipo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /** JSON con datos estructurados del diagnóstico (flexible) */
    @Column(columnDefinition = "TEXT")
    private String datos;

    @Column(length = 50)
    private String fechaDiagnostico;

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
