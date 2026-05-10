package com.vetsync.app.dto.request;

import com.vetsync.app.entity.HistorialPago;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class HistorialPagoRequest {

    @NotNull(message = "La factura es obligatoria")
    private Long facturaId;

    @NotNull(message = "El administrador es obligatorio")
    private Long administradorId;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal monto;

    @NotNull
    private HistorialPago.MetodoPago metodoPago;

    @Size(max = 200)
    private String observaciones;
}
