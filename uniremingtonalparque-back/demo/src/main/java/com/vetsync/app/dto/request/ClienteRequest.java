package com.vetsync.app.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ClienteRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "El documento es obligatorio")
    @Size(max = 20)
    private String documento;

    @Size(max = 15)
    private String telefono;

    @Size(max = 150)
    private String direccion;

    @Email(message = "Email inválido")
    @Size(max = 80)
    private String email;
}
