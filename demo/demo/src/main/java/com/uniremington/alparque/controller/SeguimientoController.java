package com.uniremington.alparque.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uniremington.alparque.dto.request.SeguimientoRequestDTO;
import com.uniremington.alparque.dto.response.SeguimientoResponseDTO;
import com.uniremington.alparque.service.SeguimientoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/seguimientos")
@RequiredArgsConstructor
public class SeguimientoController {

	private final SeguimientoService seguimientoService;

	@GetMapping
	public ResponseEntity<List<SeguimientoResponseDTO>> getAll(@RequestParam(required = false) UUID casoId) {
		if (casoId != null) {
			return ResponseEntity.ok(seguimientoService.findByCasoId(casoId));
		}
		return ResponseEntity.ok(seguimientoService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<SeguimientoResponseDTO> getById(@PathVariable UUID id) {
		return ResponseEntity.ok(seguimientoService.findById(id));
	}

	@PostMapping
	public ResponseEntity<SeguimientoResponseDTO> create(@Valid @RequestBody SeguimientoRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(seguimientoService.create(request));
	}

	@PutMapping("/{id}")
	public ResponseEntity<SeguimientoResponseDTO> update(@PathVariable UUID id,
														 @Valid @RequestBody SeguimientoRequestDTO request) {
		return ResponseEntity.ok(seguimientoService.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		seguimientoService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
