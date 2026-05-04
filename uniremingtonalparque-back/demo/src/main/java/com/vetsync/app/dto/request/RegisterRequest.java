package com.vetsync.app.dto.request;

import com.vetsync.app.entity.Usuario;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank @Size(max=100)
    private String nombre;
    @NotBlank @Email @Size(max=80)
    private String email;
    @NotBlank @Size(min=6, max=100)
    private String password;
    @NotNull
    private Usuario.Rol rol;
}
