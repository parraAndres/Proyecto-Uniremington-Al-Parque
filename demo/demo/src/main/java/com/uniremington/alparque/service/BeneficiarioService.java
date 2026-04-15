package com.uniremington.alparque.service;

import java.util.List;
import java.util.UUID;

import com.uniremington.alparque.dto.request.BeneficiarioRequestDTO;
import com.uniremington.alparque.dto.response.BeneficiarioResponseDTO;

public interface BeneficiarioService {

	List<BeneficiarioResponseDTO> findAll();

	BeneficiarioResponseDTO findById(UUID id);

	BeneficiarioResponseDTO create(BeneficiarioRequestDTO request);

	BeneficiarioResponseDTO update(UUID id, BeneficiarioRequestDTO request);

	void delete(UUID id);
}
