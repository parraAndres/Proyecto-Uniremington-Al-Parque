package com.vetsync.app.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FacturaRequest {

    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;

    private Long citaId;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal subtotal;

    @DecimalMin("0.00")
    private BigDecimal impuesto;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal total;

    @NotNull
    private LocalDate fechaEmision;
}
