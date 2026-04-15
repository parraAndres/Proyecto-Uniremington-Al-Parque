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
import org.springframework.web.bind.annotation.RestController;

import com.uniremington.alparque.dto.request.DiagnosticoRequestDTO;
import com.uniremington.alparque.dto.response.DiagnosticoResponseDTO;
import com.uniremington.alparque.service.DiagnosticoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/diagnosticos")
@RequiredArgsConstructor
public class DiagnosticoController {

	private final DiagnosticoService diagnosticoService;

	@GetMapping
	public ResponseEntity<List<DiagnosticoResponseDTO>> getAll() {
		return ResponseEntity.ok(diagnosticoService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<DiagnosticoResponseDTO> getById(@PathVariable UUID id) {
		return ResponseEntity.ok(diagnosticoService.findById(id));
	}

	@PostMapping
	public ResponseEntity<DiagnosticoResponseDTO> create(@Valid @RequestBody DiagnosticoRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(diagnosticoService.create(request));
	}

	@PutMapping("/{id}")
	public ResponseEntity<DiagnosticoResponseDTO> update(@PathVariable UUID id,
														 @Valid @RequestBody DiagnosticoRequestDTO request) {
		return ResponseEntity.ok(diagnosticoService.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		diagnosticoService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
