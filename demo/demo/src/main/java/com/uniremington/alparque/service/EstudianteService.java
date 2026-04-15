package com.uniremington.alparque.service;

import java.util.List;
import java.util.UUID;

import com.uniremington.alparque.dto.request.EstudianteRequestDTO;
import com.uniremington.alparque.dto.response.EstudianteResponseDTO;

public interface EstudianteService {

	List<EstudianteResponseDTO> findAll();

	EstudianteResponseDTO findById(UUID id);

	EstudianteResponseDTO create(EstudianteRequestDTO request);

	EstudianteResponseDTO update(UUID id, EstudianteRequestDTO request);

	void delete(UUID id);
}
