package com.vetsync.app.uniremington.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Seguimiento de un caso social de un beneficiario.
 * El ID es un UUID generado en el frontend (crypto.randomUUID()).
 */
@Entity
@Table(name = "uni_seguimientos", indexes = {
        @Index(name = "idx_uni_seg_beneficiario", columnList = "beneficiarioId"),
        @Index(name = "idx_uni_seg_estado_fecha",  columnList = "estadoCaso,fechaSeguimiento")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seguimiento {

    @Id
    @Column(name = "id", length = 36, updatable = false, nullable = false)
    private String id;

    @NotBlank
    @Column(nullable = false, length = 36)
    private String beneficiarioId;

    @Column(length = 20)
    private String estadoCaso;

    @Column(columnDefinition = "TEXT")
    private String evolucion;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(length = 50)
    private String fechaSeguimiento;

    /** Datos extra en formato JSON plano (flexible para el frontend) */
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
