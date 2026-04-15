package com.uniremington.alparque.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uniremington.alparque.dto.request.SincronizacionBatchRequestDTO;
import com.uniremington.alparque.dto.response.SincronizacionLotePageResponseDTO;
import com.uniremington.alparque.dto.response.SincronizacionResponseDTO;
import com.uniremington.alparque.service.SincronizacionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sincronizacion")
@RequiredArgsConstructor
public class SincronizacionController {

	private final SincronizacionService sincronizacionService;

	@PostMapping("/batch")
	public ResponseEntity<SincronizacionResponseDTO> sincronizarBatch(
			@Valid @RequestBody SincronizacionBatchRequestDTO request) {
		return ResponseEntity.ok(sincronizacionService.sincronizarBatch(request));
	}

	@GetMapping("/lotes")
	public ResponseEntity<SincronizacionResponseDTO> consultarLote(
			@RequestParam String loteId,
			@RequestParam String dispositivoId) {
		return ResponseEntity.ok(sincronizacionService.consultarLote(loteId, dispositivoId));
	}

	@GetMapping("/lotes/recientes")
	public ResponseEntity<SincronizacionLotePageResponseDTO> listarLotesRecientes(
			@RequestParam String dispositivoId,
			@RequestParam(required = false) String estadoLote,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		return ResponseEntity.ok(sincronizacionService.listarLotesRecientes(dispositivoId, estadoLote, page, size));
	}

	@GetMapping("/lotes/historial")
	public ResponseEntity<SincronizacionLotePageResponseDTO> listarHistorialLotes(
			@RequestParam String dispositivoId,
			@RequestParam(required = false) String estadoLote,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		return ResponseEntity.ok(
			sincronizacionService.listarHistorialLotes(dispositivoId, estadoLote, fechaInicio, fechaFin, page, size));
	}
}
