package com.vetsync.app.dto.response;

import com.vetsync.app.entity.LoteSincronizacion;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class SincronizacionResponse {
    private String claveIdempotencia;
    private LoteSincronizacion.EstadoLote estado;
    private Integer totalRegistros;
    private Integer procesados;
    private Integer duplicados;
    private Integer errores;
    private LocalDateTime fechaProcesado;
    private String mensaje;
}
