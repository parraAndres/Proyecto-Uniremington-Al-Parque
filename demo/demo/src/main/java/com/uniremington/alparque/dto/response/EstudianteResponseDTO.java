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
public class EstudianteResponseDTO {

    private UUID id;
    private String nombreCompleto;
    private String numeroDocumento;
    private String programaAcademico;
    private Integer horasParticipacion;
    private LocalDateTime fechaRegistro;
}