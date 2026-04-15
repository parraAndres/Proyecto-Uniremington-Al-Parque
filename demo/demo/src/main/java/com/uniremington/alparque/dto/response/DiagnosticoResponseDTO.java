package com.uniremington.alparque.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.uniremington.alparque.model.enums.Clasificacion;
import com.uniremington.alparque.model.enums.Prioridad;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosticoResponseDTO {

    private UUID id;
    private String municipio;
    private String problematica;
    private Clasificacion clasificacion;
    private Prioridad prioridad;
    private LocalDateTime fechaRegistro;
}