package com.uniremington.alparque.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IndicadorTendenciaResponseDTO {

    private String fechaInicio;
    private String fechaFin;
    private List<IndicadorMensualDTO> serieMensual;
}