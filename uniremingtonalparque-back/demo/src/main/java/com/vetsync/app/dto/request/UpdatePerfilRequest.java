package com.vetsync.app.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdatePerfilRequest {
    @NotBlank @Size(max=100)
    private String nombre;
    @Size(min=6, max=100)
    private String newPassword;
}
