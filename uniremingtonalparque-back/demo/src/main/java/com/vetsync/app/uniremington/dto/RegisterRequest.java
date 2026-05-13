package com.vetsync.app.uniremington.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** DTO para el registro de un nuevo usuario Uniremington. */
@Data
public class RegisterRequest {

    @NotBlank(message = "El correo electrónico es obligatorio")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
