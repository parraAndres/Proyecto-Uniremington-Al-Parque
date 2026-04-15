package com.uniremington.alparque.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.uniremington.alparque.dto.response.IndicadorMensualDTO;
import com.uniremington.alparque.dto.response.IndicadorResponseDTO;
import com.uniremington.alparque.dto.response.IndicadorTendenciaFacultadResponseDTO;
import com.uniremington.alparque.dto.response.IndicadorTendenciaResponseDTO;

public interface IndicadorService {

	IndicadorResponseDTO getResumen();

	IndicadorResponseDTO getResumen(LocalDateTime fechaInicio, LocalDateTime fechaFin);

	IndicadorTendenciaResponseDTO getTendenciaMensual(LocalDate fechaInicio, LocalDate fechaFin);

	IndicadorTendenciaFacultadResponseDTO getTendenciaMensualPorFacultad(LocalDate fechaInicio, LocalDate fechaFin);

	List<IndicadorMensualDTO> getTendenciaMensual();
}
