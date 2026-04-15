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

import com.uniremington.alparque.dto.request.BeneficiarioRequestDTO;
import com.uniremington.alparque.dto.response.BeneficiarioResponseDTO;
import com.uniremington.alparque.service.BeneficiarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/beneficiarios")
@RequiredArgsConstructor
public class BeneficiarioController {

	private final BeneficiarioService beneficiarioService;

	@GetMapping
	public ResponseEntity<List<BeneficiarioResponseDTO>> getAll() {
		return ResponseEntity.ok(beneficiarioService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<BeneficiarioResponseDTO> getById(@PathVariable UUID id) {
		return ResponseEntity.ok(beneficiarioService.findById(id));
	}

	@PostMapping
	public ResponseEntity<BeneficiarioResponseDTO> create(@Valid @RequestBody BeneficiarioRequestDTO request) {
		BeneficiarioResponseDTO response = beneficiarioService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<BeneficiarioResponseDTO> update(@PathVariable UUID id,
														  @Valid @RequestBody BeneficiarioRequestDTO request) {
		return ResponseEntity.ok(beneficiarioService.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		beneficiarioService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
