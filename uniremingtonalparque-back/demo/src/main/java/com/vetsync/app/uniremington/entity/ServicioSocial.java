package com.vetsync.app.uniremington.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "servicios_social", indexes = {
        @Index(name = "idx_servicio_facultad_fecha", columnList = "facultad,fechaServicio"),
        @Index(name = "idx_servicio_tipo", columnList = "tipoServicio")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicioSocial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "beneficiario_id", nullable = false)
    private Beneficiario beneficiario;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String facultad;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String tipoServicio;

    @Column(length = 80)
    private String resultadoAtencion;

    @Column(length = 36)
    private String estudianteId; // ID del estudiante que realizó la atención

    @NotNull
    @Column(nullable = false)
    private LocalDateTime fechaServicio;

    @Column(length = 50)
    private String estado = "ABIERTO"; // ABIERTO, EN_PROCESO, FINALIZADO

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    private Integer duracionMinutos;

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
