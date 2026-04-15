package com.uniremington.alparque.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uniremington.alparque.dto.response.IndicadorMensualDTO;
import com.uniremington.alparque.dto.response.IndicadorResponseDTO;
import com.uniremington.alparque.dto.response.IndicadorTendenciaFacultadResponseDTO;
import com.uniremington.alparque.dto.response.IndicadorTendenciaResponseDTO;
import com.uniremington.alparque.service.IndicadorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/indicadores")
@RequiredArgsConstructor
public class IndicadorController {

	private final IndicadorService indicadorService;

	@GetMapping("/resumen")
	public ResponseEntity<IndicadorResponseDTO> getResumen(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

		if (fechaInicio == null && fechaFin == null) {
			return ResponseEntity.ok(indicadorService.getResumen());
		}

		if (fechaInicio == null || fechaFin == null) {
			throw new IllegalArgumentException("Debe enviar fechaInicio y fechaFin para filtrar por rango");
		}

		LocalDateTime inicio = fechaInicio.atStartOfDay();
		LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);
		return ResponseEntity.ok(indicadorService.getResumen(inicio, fin));
	}

	@GetMapping("/tendencia")
	public ResponseEntity<IndicadorTendenciaResponseDTO> getTendenciaMensual(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
		return ResponseEntity.ok(indicadorService.getTendenciaMensual(fechaInicio, fechaFin));
	}

	@GetMapping("/tendencia/serie")
	public ResponseEntity<List<IndicadorMensualDTO>> getSerieMensual() {
		return ResponseEntity.ok(indicadorService.getTendenciaMensual());
	}

	@GetMapping("/tendencia/facultad")
	public ResponseEntity<IndicadorTendenciaFacultadResponseDTO> getTendenciaMensualPorFacultad(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
		return ResponseEntity.ok(indicadorService.getTendenciaMensualPorFacultad(fechaInicio, fechaFin));
	}
}
