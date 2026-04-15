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

import com.uniremington.alparque.dto.request.RecursoRequestDTO;
import com.uniremington.alparque.dto.response.RecursoResponseDTO;
import com.uniremington.alparque.service.RecursoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recursos")
@RequiredArgsConstructor
public class RecursoController {

	private final RecursoService recursoService;

	@GetMapping
	public ResponseEntity<List<RecursoResponseDTO>> getAll() {
		return ResponseEntity.ok(recursoService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<RecursoResponseDTO> getById(@PathVariable UUID id) {
		return ResponseEntity.ok(recursoService.findById(id));
	}

	@PostMapping
	public ResponseEntity<RecursoResponseDTO> create(@Valid @RequestBody RecursoRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(recursoService.create(request));
	}

	@PutMapping("/{id}")
	public ResponseEntity<RecursoResponseDTO> update(@PathVariable UUID id,
													 @Valid @RequestBody RecursoRequestDTO request) {
		return ResponseEntity.ok(recursoService.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		recursoService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
