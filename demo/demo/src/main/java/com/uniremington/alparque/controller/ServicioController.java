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

import com.uniremington.alparque.dto.request.ServicioRequestDTO;
import com.uniremington.alparque.dto.response.ServicioResponseDTO;
import com.uniremington.alparque.service.ServicioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/servicios")
@RequiredArgsConstructor
public class ServicioController {

	private final ServicioService servicioService;

	@GetMapping
	public ResponseEntity<List<ServicioResponseDTO>> getAll(@RequestParam(required = false) UUID beneficiarioId) {
		if (beneficiarioId != null) {
			return ResponseEntity.ok(servicioService.findByBeneficiarioId(beneficiarioId));
		}
		return ResponseEntity.ok(servicioService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ServicioResponseDTO> getById(@PathVariable UUID id) {
		return ResponseEntity.ok(servicioService.findById(id));
	}

	@PostMapping
	public ResponseEntity<ServicioResponseDTO> create(@Valid @RequestBody ServicioRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(servicioService.create(request));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ServicioResponseDTO> update(@PathVariable UUID id,
													  @Valid @RequestBody ServicioRequestDTO request) {
		return ResponseEntity.ok(servicioService.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		servicioService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
