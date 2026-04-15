package com.uniremington.alparque.service;

import java.util.List;
import java.util.UUID;

import com.uniremington.alparque.dto.request.ServicioRequestDTO;
import com.uniremington.alparque.dto.response.ServicioResponseDTO;

public interface ServicioService {

	List<ServicioResponseDTO> findAll();

	ServicioResponseDTO findById(UUID id);

	List<ServicioResponseDTO> findByBeneficiarioId(UUID beneficiarioId);

	ServicioResponseDTO create(ServicioRequestDTO request);

	ServicioResponseDTO update(UUID id, ServicioRequestDTO request);

	void delete(UUID id);
}
