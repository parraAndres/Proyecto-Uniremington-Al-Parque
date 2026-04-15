package com.uniremington.alparque.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uniremington.alparque.dto.request.BeneficiarioRequestDTO;
import com.uniremington.alparque.dto.response.BeneficiarioResponseDTO;
import com.uniremington.alparque.exception.DuplicateRecordException;
import com.uniremington.alparque.exception.ResourceNotFoundException;
import com.uniremington.alparque.mapper.BeneficiarioMapper;
import com.uniremington.alparque.model.Beneficiario;
import com.uniremington.alparque.repository.BeneficiarioRepository;
import com.uniremington.alparque.service.BeneficiarioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BeneficiarioServiceImpl implements BeneficiarioService {

	private final BeneficiarioRepository beneficiarioRepository;
	private final BeneficiarioMapper beneficiarioMapper;

	@Override
	@Transactional(readOnly = true)
	public List<BeneficiarioResponseDTO> findAll() {
		return beneficiarioRepository.findAll()
			.stream()
			.map(beneficiarioMapper::toResponseDTO)
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public BeneficiarioResponseDTO findById(UUID id) {
		return beneficiarioMapper.toResponseDTO(findEntityById(id));
	}

	@Override
	public BeneficiarioResponseDTO create(BeneficiarioRequestDTO request) {
		validateNumeroDocumentoDisponible(request.getNumeroDocumento(), null);

		Beneficiario beneficiario = beneficiarioMapper.toEntity(request);
		Beneficiario savedBeneficiario = saveBeneficiario(beneficiario);
		return beneficiarioMapper.toResponseDTO(savedBeneficiario);
	}

	@Override
	public BeneficiarioResponseDTO update(UUID id, BeneficiarioRequestDTO request) {
		Beneficiario beneficiario = findEntityById(id);
		validateNumeroDocumentoDisponible(request.getNumeroDocumento(), id);

		beneficiarioMapper.updateEntityFromRequest(request, beneficiario);
		Beneficiario savedBeneficiario = saveBeneficiario(beneficiario);
		return beneficiarioMapper.toResponseDTO(savedBeneficiario);
	}

	@Override
	public void delete(UUID id) {
		Beneficiario beneficiario = findEntityById(id);
		beneficiarioRepository.delete(Objects.requireNonNull(beneficiario));
	}

	private Beneficiario findEntityById(UUID id) {
		UUID requiredId = Objects.requireNonNull(id, "El id del beneficiario es obligatorio");
		return beneficiarioRepository.findById(requiredId)
			.orElseThrow(() -> new ResourceNotFoundException("Beneficiario no encontrado con id: " + id));
	}

	private void validateNumeroDocumentoDisponible(String numeroDocumento, UUID id) {
		boolean exists = id == null
			? beneficiarioRepository.existsByNumeroDocumento(numeroDocumento)
			: beneficiarioRepository.existsByNumeroDocumentoAndIdNot(numeroDocumento, id);

		if (exists) {
			throw new DuplicateRecordException("Ya existe un beneficiario con el numero de documento: " + numeroDocumento);
		}
	}

	@SuppressWarnings("null")
	private Beneficiario saveBeneficiario(Beneficiario beneficiario) {
		return beneficiarioRepository.save(beneficiario);
	}
}
