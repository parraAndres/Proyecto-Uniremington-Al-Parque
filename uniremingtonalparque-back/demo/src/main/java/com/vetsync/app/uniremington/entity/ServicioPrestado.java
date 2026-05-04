package com.vetsync.app.uniremington.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Servicio prestado a un beneficiario durante la jornada social.
 * El ID es un UUID generado en el frontend (crypto.randomUUID()).
 * La relación al beneficiario se guarda como String (beneficiarioId) para
 * soportar la sincronización offline sin necesidad de que el beneficiario
 * exista previamente en la BD.
 */
@Entity
@Table(name = "uni_servicios_prestados", indexes = {
        @Index(name = "idx_uni_serv_beneficiario", columnList = "beneficiarioId"),
        @Index(name = "idx_uni_serv_facultad_fecha", columnList = "facultadResponsable,fechaAtencion"),
        @Index(name = "idx_uni_serv_tipo", columnList = "tipoServicio")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicioPrestado {

    @Id
    @Column(name = "id", length = 36, updatable = false, nullable = false)
    private String id;

    /** UUID del beneficiario (generado en el frontend) */
    @NotBlank
    @Column(nullable = false, length = 36)
    private String beneficiarioId;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String tipoServicio;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String facultadResponsable;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /** Duración de la atención en minutos */
    @Column
    private Integer tiempoAtencion;

    @Column(length = 150)
    private String resultado;

    @Column(length = 50)
    private String fechaAtencion;

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
