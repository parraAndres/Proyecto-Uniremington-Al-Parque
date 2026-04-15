package com.uniremington.alparque.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeguimientoResponseDTO {

    private UUID id;
    private UUID casoId;
    private String registroAvances;
    private LocalDateTime fechaSeguimientoProgramado;
    private LocalDateTime fechaRegistro;
}