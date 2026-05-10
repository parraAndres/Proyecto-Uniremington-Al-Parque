package com.vetsync.app.uniremington.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "seguimientos_caso", indexes = {
        @Index(name = "idx_seguimiento_caso", columnList = "casoId"),
        @Index(name = "idx_seguimiento_estado_fecha", columnList = "estadoCaso,fechaEstado")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeguimientoCaso {

    public enum EstadoCaso { ABIERTO, CERRADO }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "beneficiario_id", nullable = false)
    private Beneficiario beneficiario;

    @NotBlank
    @Column(nullable = false, length = 60)
    private String casoId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCaso estadoCaso;

    @Column(columnDefinition = "TEXT")
    private String evolucion;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime fechaEstado;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
