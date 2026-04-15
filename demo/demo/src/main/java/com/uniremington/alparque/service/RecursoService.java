package com.uniremington.alparque.service;

import java.util.List;
import java.util.UUID;

import com.uniremington.alparque.dto.request.RecursoRequestDTO;
import com.uniremington.alparque.dto.response.RecursoResponseDTO;

public interface RecursoService {

	List<RecursoResponseDTO> findAll();

	RecursoResponseDTO findById(UUID id);

	RecursoResponseDTO create(RecursoRequestDTO request);

	RecursoResponseDTO update(UUID id, RecursoRequestDTO request);

	void delete(UUID id);
}
