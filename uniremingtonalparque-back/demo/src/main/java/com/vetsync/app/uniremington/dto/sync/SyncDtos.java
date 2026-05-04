package com.vetsync.app.uniremington.dto.sync;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTOs de sincronización para las entidades del módulo Uniremington al Parque.
 * Cada DTO corresponde a un endpoint POST /api/sync/{entidad}.
 *
 * Todos los campos id son UUIDs generados en el frontend con crypto.randomUUID().
 */
public class SyncDtos {

    // ──────────────────────────────────────────────────────────────────
    //  Beneficiario
    // ──────────────────────────────────────────────────────────────────

    @Data
    public static class BeneficiarioDto {
        @NotBlank(message = "El id es obligatorio")
        private String id;          // UUID del frontend

        @NotBlank(message = "El nombre es obligatorio")
        private String nombre;

        @NotBlank(message = "El documento es obligatorio")
        private String documento;

        private Integer edad;
        private String genero;
        private String telefono;
        private String municipio;
        private String barrio;
        private String tipoPoblacion;
        private String servicioSolicitado;
        private Boolean autorizaDatos;
        private String fechaRegistro;
    }

    // ──────────────────────────────────────────────────────────────────
    //  Servicio Prestado
    // ──────────────────────────────────────────────────────────────────

    @Data
    public static class ServicioDto {
        @NotBlank(message = "El id es obligatorio")
        private String id;

        @NotBlank(message = "El beneficiarioId es obligatorio")
        private String beneficiarioId;

        @NotBlank(message = "El tipoServicio es obligatorio")
        private String tipoServicio;

        @NotBlank(message = "La facultadResponsable es obligatoria")
        private String facultadResponsable;

        private String descripcion;
        private Integer tiempoAtencion;
        private String resultado;
        private String fechaAtencion;
    }

    // ──────────────────────────────────────────────────────────────────
    //  Seguimiento
    // ──────────────────────────────────────────────────────────────────

    @Data
    public static class SeguimientoDto {
        @NotBlank(message = "El id es obligatorio")
        private String id;

        @NotBlank(message = "El beneficiarioId es obligatorio")
        private String beneficiarioId;

        private String estadoCaso;
        private String evolucion;
        private String observaciones;
        private String fechaSeguimiento;
        private String datosExtra;
    }

    // ──────────────────────────────────────────────────────────────────
    //  Diagnóstico
    // ──────────────────────────────────────────────────────────────────

    @Data
    public static class DiagnosticoDto {
        @NotBlank(message = "El id es obligatorio")
        private String id;

        @NotBlank(message = "El beneficiarioId es obligatorio")
        private String beneficiarioId;

        private String tipo;
        private String descripcion;
        private String datos;       // JSON plano / texto libre
        private String fechaDiagnostico;
    }

    // ──────────────────────────────────────────────────────────────────
    //  Académico
    // ──────────────────────────────────────────────────────────────────

    @Data
    public static class AcademicoDto {
        @NotBlank(message = "El id es obligatorio")
        private String id;

        private String estudianteId;
        private String nombreEstudiante;
        private String programa;
        private String facultad;
        private BigDecimal horasReportadas;
        private String fechaActividad;
        private String tipoParticipacion;
        private String datosExtra;
    }

    // ──────────────────────────────────────────────────────────────────
    //  Recurso / Aporte
    // ──────────────────────────────────────────────────────────────────

    @Data
    public static class RecursoDto {
        @NotBlank(message = "El id es obligatorio")
        private String id;

        private String tipoAporte;
        private String fuente;
        private String aportante;
        private String descripcion;
        private BigDecimal valorMonetario;
        private BigDecimal cantidad;
        private String unidadMedida;
        private String fechaRegistro;
        private String facultadAsociada;
        private String datosExtra;
    }

    // ──────────────────────────────────────────────────────────────────
    //  Respuesta genérica de sincronización
    // ──────────────────────────────────────────────────────────────────

    @Data
    public static class SyncItemResult {
        private String id;
        private String status;    // "CREATED" | "UPDATED" | "ERROR"
        private String message;

        public static SyncItemResult created(String id) {
            SyncItemResult r = new SyncItemResult();
            r.id = id; r.status = "CREATED"; r.message = "Registro creado";
            return r;
        }
        public static SyncItemResult updated(String id) {
            SyncItemResult r = new SyncItemResult();
            r.id = id; r.status = "UPDATED"; r.message = "Registro actualizado";
            return r;
        }
        public static SyncItemResult error(String id, String msg) {
            SyncItemResult r = new SyncItemResult();
            r.id = id; r.status = "ERROR"; r.message = msg;
            return r;
        }
    }
}
