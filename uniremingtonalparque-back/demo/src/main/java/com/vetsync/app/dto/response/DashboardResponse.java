package com.vetsync.app.dto.response;

import com.vetsync.app.entity.Cita;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class DashboardResponse {

    @Data @Builder
    public static class MetricasAdmin {
        private long totalClientes;
        private long totalMascotas;
        private BigDecimal facturacionMes;
        private long facturasPendientes;
        private long citasHoy;
        private long productosStockBajo;
        private List<IngresosDia> ingresosUltimos7Dias;
        private List<DistribucionRol> distribucionRoles;
    }

    @Data @Builder
    public static class MetricasVeterinario {
        private long citasProgramadasHoy;
        private long citasCompletadasMes;
        private long historiasPendientes;
        private long formulasEmitidas;
        private List<CitaResumen> proximasCitas;
    }

    @Data @Builder
    public static class MetricasFarmaceutico {
        private long formulasPendientes;
        private long formulasDispensadasHoy;
        private long productosStockBajo;
        private long totalProductos;
        private List<AlertaStock> alertasStock;
    }

    @Data @Builder
    public static class MetricasAuxiliar {
        private long citasHoy;
        private long clientesRegistradosMes;
        private long mascotasRegistradasMes;
        private List<CitaResumen> proximasCitas;
    }

    @Data @Builder
    public static class IngresosDia {
        private String fecha;
        private BigDecimal total;
    }

    @Data @Builder
    public static class DistribucionRol {
        private String rol;
        private long cantidad;
    }

    @Data @Builder
    public static class CitaResumen {
        private Long id;
        private String mascotaNombre;
        private String clienteNombre;
        private String veterinarioNombre;
        private LocalDateTime fechaHora;
        private Cita.EstadoCita estado;
        private String motivo;
    }

    @Data @Builder
    public static class AlertaStock {
        private Long productoId;
        private String codigo;
        private String nombre;
        private Integer stockActual;
        private Integer stockMinimo;
    }
}
