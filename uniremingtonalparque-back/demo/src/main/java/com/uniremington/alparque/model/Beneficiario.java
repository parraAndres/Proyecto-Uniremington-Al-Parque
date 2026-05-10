package com.uniremington.alparque.model;

import com.uniremington.alparque.model.enums.Genero;
import com.uniremington.alparque.model.enums.TipoPoblacion;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Beneficiario atendido en la jornada "Uniremington al Parque".
 * El ID es un UUID generado por el servidor al registrar el beneficiario.
 */
@Entity(name = "AlparqueBeneficiario")
@Table(name = "alparque_beneficiarios", indexes = {
        @Index(name = "idx_alp_ben_documento", columnList = "numero_documento", unique = true),
        @Index(name = "idx_alp_ben_municipio",  columnList = "municipio")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Beneficiario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "numero_documento", nullable = false, length = 30, unique = true)
    private String numeroDocumento;

    @Column(name = "edad")
    private Integer edad;

    @Enumerated(EnumType.STRING)
    @Column(name = "genero", length = 30)
    private Genero genero;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "municipio", nullable = false, length = 100)
    private String municipio;

    @Column(name = "barrio_vereda", length = 100)
    private String barrioVereda;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_poblacion", length = 50)
    private TipoPoblacion tipoPoblacion;

    @Column(name = "servicio_solicitado", length = 150)
    private String servicioSolicitado;

    @Column(name = "autoriza_datos")
    private Boolean autorizaDatos;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
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
