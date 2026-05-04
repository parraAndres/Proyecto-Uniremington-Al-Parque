package com.vetsync.app.uniremington.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recursos_aporte", indexes = {
        @Index(name = "idx_recurso_tipo_fecha", columnList = "tipoAporte,fechaRegistro"),
        @Index(name = "idx_recurso_facultad", columnList = "facultadAsociada")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecursoAporte {

    public enum TipoAporte { DINERO, ESPECIE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoAporte tipoAporte;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String fuente;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String aportante;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(precision = 14, scale = 2)
    private BigDecimal valorMonetario;

    @Column(precision = 12, scale = 2)
    private BigDecimal cantidad;

    @Column(length = 40)
    private String unidadMedida;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(length = 80)
    private String facultadAsociada;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
