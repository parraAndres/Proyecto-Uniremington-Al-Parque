package com.uniremington.alparque.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uniremington.alparque.dto.request.ServicioRequestDTO;
import com.uniremington.alparque.dto.response.ServicioResponseDTO;
import com.uniremington.alparque.exception.ResourceNotFoundException;
import com.uniremington.alparque.mapper.ServicioMapper;
import com.uniremington.alparque.model.Beneficiario;
import com.uniremington.alparque.model.Servicio;
import com.uniremington.alparque.repository.BeneficiarioRepository;
import com.uniremington.alparque.repository.ServicioRepository;
import com.uniremington.alparque.service.ServicioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ServicioServiceImpl implements ServicioService {

	private final ServicioRepository servicioRepository;
	private final BeneficiarioRepository beneficiarioRepository;
	private final ServicioMapper servicioMapper;

	@Override
	@Transactional(readOnly = true)
	public List<ServicioResponseDTO> findAll() {
		return servicioRepository.findAll()
			.stream()
			.map(servicioMapper::toResponseDTO)
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public ServicioResponseDTO findById(UUID id) {
		return servicioMapper.toResponseDTO(findEntityById(id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<ServicioResponseDTO> findByBeneficiarioId(UUID beneficiarioId) {
		Beneficiario beneficiario = findBeneficiarioById(beneficiarioId);
		return servicioRepository.findByBeneficiarioId(beneficiario.getId())
			.stream()
			.map(servicioMapper::toResponseDTO)
			.toList();
	}

	@Override
	public ServicioResponseDTO create(ServicioRequestDTO request) {
		Servicio servicio = servicioMapper.toEntity(request);
		servicio.setBeneficiario(findBeneficiarioById(request.getBeneficiarioId()));
		Servicio savedServicio = saveServicio(servicio);
		return servicioMapper.toResponseDTO(savedServicio);
	}

	@Override
	public ServicioResponseDTO update(UUID id, ServicioRequestDTO request) {
		Servicio servicio = findEntityById(id);
		servicioMapper.updateEntityFromRequest(request, servicio);
		servicio.setBeneficiario(findBeneficiarioById(request.getBeneficiarioId()));
		Servicio savedServicio = saveServicio(servicio);
		return servicioMapper.toResponseDTO(savedServicio);
	}

	@Override
	public void delete(UUID id) {
		Servicio servicio = findEntityById(id);
		servicioRepository.delete(Objects.requireNonNull(servicio));
	}

	private Servicio findEntityById(UUID id) {
		UUID requiredId = Objects.requireNonNull(id, "El id del servicio es obligatorio");
		return servicioRepository.findById(requiredId)
			.orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con id: " + id));
	}

	private Beneficiario findBeneficiarioById(UUID beneficiarioId) {
		UUID requiredId = Objects.requireNonNull(beneficiarioId, "El id del beneficiario es obligatorio");
		return beneficiarioRepository.findById(requiredId)
			.orElseThrow(() -> new ResourceNotFoundException("Beneficiario no encontrado con id: " + beneficiarioId));
	}

	@SuppressWarnings("null")
	private Servicio saveServicio(Servicio servicio) {
		return servicioRepository.save(servicio);
	}
}
