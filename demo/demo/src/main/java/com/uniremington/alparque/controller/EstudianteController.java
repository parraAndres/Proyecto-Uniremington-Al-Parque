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

import com.uniremington.alparque.dto.request.EstudianteRequestDTO;
import com.uniremington.alparque.dto.response.EstudianteResponseDTO;
import com.uniremington.alparque.service.EstudianteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/estudiantes")
@RequiredArgsConstructor
public class EstudianteController {

	private final EstudianteService estudianteService;

	@GetMapping
	public ResponseEntity<List<EstudianteResponseDTO>> getAll() {
		return ResponseEntity.ok(estudianteService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<EstudianteResponseDTO> getById(@PathVariable UUID id) {
		return ResponseEntity.ok(estudianteService.findById(id));
	}

	@PostMapping
	public ResponseEntity<EstudianteResponseDTO> create(@Valid @RequestBody EstudianteRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(estudianteService.create(request));
	}

	@PutMapping("/{id}")
	public ResponseEntity<EstudianteResponseDTO> update(@PathVariable UUID id,
														@Valid @RequestBody EstudianteRequestDTO request) {
		return ResponseEntity.ok(estudianteService.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		estudianteService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
