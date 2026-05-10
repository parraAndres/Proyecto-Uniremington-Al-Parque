package com.vetsync.app.uniremington.dto;

import com.vetsync.app.uniremington.entity.RecursoAporte;
import com.vetsync.app.uniremington.entity.SeguimientoCaso;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DomainRequests {

    @Data
    public static class BeneficiarioRequest {
        @NotBlank
        private String documento;
        @NotBlank
        private String nombres;
        @NotBlank
        private String apellidos;
        private String telefono;
        private String direccion;
        @NotBlank
        private String municipio;
        private String barrio;
        @NotNull
        private Boolean consentimientoDatos;
    }

    @Data
    public static class ServicioRequest {
        @NotBlank
        private String beneficiarioDocumento;
        @NotBlank
        private String facultad;
        @NotBlank
        private String tipoServicio;
        private String resultadoAtencion;
        @NotNull
        private LocalDateTime fechaServicio;
        private String observaciones;
    }

    @Data
    public static class SeguimientoRequest {
        @NotBlank
        private String beneficiarioDocumento;
        @NotBlank
        private String casoId;
        @NotNull
        private SeguimientoCaso.EstadoCaso estadoCaso;
        private String evolucion;
        @NotNull
        private LocalDateTime fechaEstado;
    }

    @Data
    public static class AcademicoRequest {
        @NotBlank
        private String estudianteId;
        @NotBlank
        private String nombreEstudiante;
        @NotBlank
        private String programa;
        @NotBlank
        private String facultad;
        @NotNull
        @DecimalMin("0.0")
        private BigDecimal horasReportadas;
        @NotNull
        private LocalDate fechaActividad;
        private String tipoParticipacion;
    }

    @Data
    public static class RecursoRequest {
        @NotNull
        private RecursoAporte.TipoAporte tipoAporte;
        @NotBlank
        private String fuente;
        @NotBlank
        private String aportante;
        private String descripcion;
        private BigDecimal valorMonetario;
        private BigDecimal cantidad;
        private String unidadMedida;
        @NotNull
        private LocalDateTime fechaRegistro;
        private String facultadAsociada;
    }
}
