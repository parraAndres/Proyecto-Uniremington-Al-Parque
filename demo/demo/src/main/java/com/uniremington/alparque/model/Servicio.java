package com.uniremington.alparque.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.uniremington.alparque.model.enums.Facultad;
import com.uniremington.alparque.model.enums.ResultadoAtencion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "servicios")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Servicio {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "El beneficiario es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiario_id", nullable = false)
    private Beneficiario beneficiario;  // Relación con Beneficiario

    @NotBlank(message = "El tipo de servicio es obligatorio")
    @Column(nullable = false, length = 100)
    private String tipoServicio;

    @NotNull(message = "La facultad es obligatoria")
    @Enumerated(EnumType.STRING)
    private Facultad facultad;

    @Column(length = 500)
    private String descripcionAtencion;

    @Min(value = 1, message = "El tiempo debe ser positivo")
    private Integer tiempoAtencionMinutos; 

    @NotNull(message = "El resultado es obligatorio")
    @Enumerated(EnumType.STRING)
    private ResultadoAtencion resultado;

    @Column(length = 500)
    private String observaciones;

    @Column(length = 255)
    private String evidencias;  // URL o path a archivos opcionales

    @CreationTimestamp
    @Column(name = "fecha_atencion", updatable = false)
    private LocalDateTime fechaAtencion;
}
