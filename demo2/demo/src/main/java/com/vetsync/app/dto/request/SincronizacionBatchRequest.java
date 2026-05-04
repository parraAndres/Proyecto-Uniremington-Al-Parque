package com.vetsync.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SincronizacionBatchRequest {

    @NotBlank(message = "La clave de idempotencia es obligatoria (UUID)")
    private String claveIdempotencia;

    @NotBlank
    private String origen;

    @NotNull
    private LocalDateTime lastModified;

    @NotNull
    private List<RegistroOfflineDto> registros;

    @Data
    public static class RegistroOfflineDto {
        private String tipo;      // CLIENTE, MASCOTA, CITA, etc.
        private String operacion; // CREATE, UPDATE
        private Long entidadId;   // ID para operaciones UPDATE
        private Object payload;
        private LocalDateTime capturedAt;
    }
}
