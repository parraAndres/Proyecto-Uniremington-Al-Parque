package com.uniremington.alparque.dto.response;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IndicadorResponseDTO {

	private long numeroBeneficiarios;
	private long numeroServicios;
	private Map<String, Long> serviciosPorFacultad;
	private Map<String, Long> coberturaTerritorialPorMunicipio;
	private Map<String, Long> serviciosPorResultado;
}
