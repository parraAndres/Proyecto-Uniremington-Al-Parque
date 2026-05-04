package com.vetsync.app.uniremington.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** DTO para el registro de un nuevo usuario Uniremington. */
@Data
public class RegisterRequest {

    @NotBlank(message = "El documento es obligatorio")
    private String documento;

    @NotBlank(message = "El nombre completo es obligatorio")
    private String nombreCompleto;

    @NotBlank(message = "La facultad es obligatoria")
    private String facultad;

    @NotBlank(message = "El programa es obligatorio")
    private String programa;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
