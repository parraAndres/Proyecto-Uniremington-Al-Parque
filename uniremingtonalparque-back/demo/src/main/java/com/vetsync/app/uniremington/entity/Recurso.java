package com.vetsync.app.uniremington.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Recurso o aporte (dinero / especie) registrado durante la jornada social.
 * El ID es un UUID generado en el frontend (crypto.randomUUID()).
 */
@Entity
@Table(name = "uni_recursos", indexes = {
        @Index(name = "idx_uni_rec_tipo_fecha",  columnList = "tipoAporte,fechaRegistro"),
        @Index(name = "idx_uni_rec_facultad",     columnList = "facultadAsociada")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recurso {

    @Id
    @Column(name = "id", length = 36, updatable = false, nullable = false)
    private String id;

    @Column(length = 20)
    private String tipoAporte;

    @Column(length = 120)
    private String fuente;

    @Column(length = 150)
    private String aportante;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(precision = 14, scale = 2)
    private BigDecimal valorMonetario;

    @Column(precision = 12, scale = 2)
    private BigDecimal cantidad;

    @Column(length = 40)
    private String unidadMedida;

    @Column(length = 50)
    private String fechaRegistro;

    @Column(length = 80)
    private String facultadAsociada;

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
