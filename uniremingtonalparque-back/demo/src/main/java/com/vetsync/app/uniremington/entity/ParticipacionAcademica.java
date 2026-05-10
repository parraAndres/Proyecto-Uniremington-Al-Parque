package com.vetsync.app.uniremington.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "participaciones_academicas", indexes = {
        @Index(name = "idx_academico_facultad_fecha", columnList = "facultad,fechaActividad"),
        @Index(name = "idx_academico_programa", columnList = "programa")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipacionAcademica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 60)
    private String estudianteId;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String nombreEstudiante;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String programa;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String facultad;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal horasReportadas;

    @NotNull
    @Column(nullable = false)
    private LocalDate fechaActividad;

    @Column(length = 80)
    private String tipoParticipacion;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
