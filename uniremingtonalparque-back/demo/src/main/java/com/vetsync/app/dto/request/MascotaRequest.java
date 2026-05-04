package com.vetsync.app.dto.request;

import com.vetsync.app.entity.Mascota;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MascotaRequest {

    @NotBlank
    @Size(max = 80)
    private String nombre;

    @NotBlank
    @Size(max = 50)
    private String especie;

    @Size(max = 50)
    private String raza;

    @NotNull
    @Min(value = 1, message = "La edad debe ser mayor a 0")
    private Integer edad;

    private Mascota.Sexo sexo;

    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;
}
