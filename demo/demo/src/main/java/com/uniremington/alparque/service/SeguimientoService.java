package com.uniremington.alparque.service;

import java.util.List;
import java.util.UUID;

import com.uniremington.alparque.dto.request.SeguimientoRequestDTO;
import com.uniremington.alparque.dto.response.SeguimientoResponseDTO;

public interface SeguimientoService {

	List<SeguimientoResponseDTO> findAll();

	SeguimientoResponseDTO findById(UUID id);

	List<SeguimientoResponseDTO> findByCasoId(UUID casoId);

	SeguimientoResponseDTO create(SeguimientoRequestDTO request);

	SeguimientoResponseDTO update(UUID id, SeguimientoRequestDTO request);

	void delete(UUID id);
}
