package com.vetsync.app.uniremington.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** DTO para el login de un usuario Uniremington por documento y contraseña. */
@Data
public class LoginRequest {

    @NotBlank(message = "El documento es obligatorio")
    private String documento;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
