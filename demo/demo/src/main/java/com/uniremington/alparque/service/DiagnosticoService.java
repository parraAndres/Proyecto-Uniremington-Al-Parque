package com.uniremington.alparque.service;

import java.util.List;
import java.util.UUID;

import com.uniremington.alparque.dto.request.DiagnosticoRequestDTO;
import com.uniremington.alparque.dto.response.DiagnosticoResponseDTO;

public interface DiagnosticoService {

	List<DiagnosticoResponseDTO> findAll();

	DiagnosticoResponseDTO findById(UUID id);

	DiagnosticoResponseDTO create(DiagnosticoRequestDTO request);

	DiagnosticoResponseDTO update(UUID id, DiagnosticoRequestDTO request);

	void delete(UUID id);
}
