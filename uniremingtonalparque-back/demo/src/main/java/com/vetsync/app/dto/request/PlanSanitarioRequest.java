package com.vetsync.app.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PlanSanitarioRequest {

    @NotNull(message = "La mascota es obligatoria")
    private Long mascotaId;

    @NotBlank
    @Size(max = 100)
    private String vacuna;

    @NotNull
    private LocalDate fechaAplicacion;

    private LocalDate proximaAplicacion;

    private String observaciones;
}
