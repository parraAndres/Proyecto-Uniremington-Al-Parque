package com.uniremington.alparque.dto.response;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IndicadorTendenciaFacultadResponseDTO {

    private String fechaInicio;
    private String fechaFin;
    private List<String> meses;
    private Map<String, List<Long>> seriesPorFacultad;
}