package com.vetsync.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FormulaMedicaRequest {

    @NotNull(message = "La historia clínica es obligatoria")
    private Long historiaClinicaId;

    @NotNull(message = "El veterinario es obligatorio")
    private Long veterinarioId;

    @NotBlank(message = "Los medicamentos son obligatorios")
    private String medicamentos;
}
