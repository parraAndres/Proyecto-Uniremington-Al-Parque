package com.vetsync.app.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CitaRequest {

    @NotNull
    private Long mascotaId;

    @NotNull
    private Long veterinarioId;

    @NotNull
    private LocalDateTime fechaHora;

    @Size(max = 200)
    private String motivo;
}
