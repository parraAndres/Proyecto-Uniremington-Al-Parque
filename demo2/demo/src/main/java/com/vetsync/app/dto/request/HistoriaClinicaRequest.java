package com.vetsync.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HistoriaClinicaRequest {

    @NotNull(message = "La cita es obligatoria")
    private Long citaId;

    private String anamnesis;

    private String diagnostico;

    private String tratamiento;
}
