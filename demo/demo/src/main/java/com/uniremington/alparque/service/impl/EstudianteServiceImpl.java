package com.uniremington.alparque.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uniremington.alparque.dto.request.EstudianteRequestDTO;
import com.uniremington.alparque.dto.response.EstudianteResponseDTO;
import com.uniremington.alparque.exception.DuplicateRecordException;
import com.uniremington.alparque.exception.ResourceNotFoundException;
import com.uniremington.alparque.mapper.EstudianteMapper;
import com.uniremington.alparque.model.Estudiante;
import com.uniremington.alparque.repository.EstudianteRepository;
import com.uniremington.alparque.service.EstudianteService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EstudianteServiceImpl implements EstudianteService {

	private final EstudianteRepository estudianteRepository;
	private final EstudianteMapper estudianteMapper;

	@Override
	@Transactional(readOnly = true)
	public List<EstudianteResponseDTO> findAll() {
		return estudianteRepository.findAll().stream().map(estudianteMapper::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public EstudianteResponseDTO findById(UUID id) {
		return estudianteMapper.toResponse(findEntity(id));
	}

	@Override
	public EstudianteResponseDTO create(EstudianteRequestDTO request) {
		validarDocumento(request.getNumeroDocumento(), null);
		Estudiante estudiante = estudianteMapper.toEntity(request);
		return estudianteMapper.toResponse(estudianteRepository.save(estudiante));
	}

	@Override
	public EstudianteResponseDTO update(UUID id, EstudianteRequestDTO request) {
		Estudiante estudiante = findEntity(id);
		validarDocumento(request.getNumeroDocumento(), id);
		estudianteMapper.updateEntity(request, estudiante);
		return estudianteMapper.toResponse(estudianteRepository.save(estudiante));
	}

	@Override
	public void delete(UUID id) {
		estudianteRepository.delete(findEntity(id));
	}

	private Estudiante findEntity(UUID id) {
		return estudianteRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con id: " + id));
	}

	private void validarDocumento(String documento, UUID id) {
		boolean existe = id == null
			? estudianteRepository.existsByNumeroDocumento(documento)
			: estudianteRepository.existsByNumeroDocumentoAndIdNot(documento, id);
		if (existe) {
			throw new DuplicateRecordException("Ya existe un estudiante con documento: " + documento);
		}
	}
}
