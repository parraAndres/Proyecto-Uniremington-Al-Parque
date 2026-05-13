package com.vetsync.app.uniremington.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** DTO para el registro de un nuevo usuario Uniremington. */
@Data
public class RegisterRequest {

    private String email;
    private String documento;
    private String identificador;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @NotBlank(message = "El nombre completo es obligatorio")
    private String nombreCompleto;

    private String genero;

    private String rol;
}
