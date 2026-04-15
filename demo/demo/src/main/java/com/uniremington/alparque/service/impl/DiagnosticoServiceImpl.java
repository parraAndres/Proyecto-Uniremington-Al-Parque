package com.uniremington.alparque.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uniremington.alparque.dto.request.DiagnosticoRequestDTO;
import com.uniremington.alparque.dto.response.DiagnosticoResponseDTO;
import com.uniremington.alparque.exception.ResourceNotFoundException;
import com.uniremington.alparque.mapper.DiagnosticoMapper;
import com.uniremington.alparque.model.Diagnostico;
import com.uniremington.alparque.repository.DiagnosticoRepository;
import com.uniremington.alparque.service.DiagnosticoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DiagnosticoServiceImpl implements DiagnosticoService {

    private final DiagnosticoRepository diagnosticoRepository;
    private final DiagnosticoMapper diagnosticoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DiagnosticoResponseDTO> findAll() {
        return diagnosticoRepository.findAll().stream().map(diagnosticoMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DiagnosticoResponseDTO findById(UUID id) {
        return diagnosticoMapper.toResponse(findEntity(id));
    }

    @Override
    public DiagnosticoResponseDTO create(DiagnosticoRequestDTO request) {
        Diagnostico diagnostico = diagnosticoMapper.toEntity(request);
        return diagnosticoMapper.toResponse(diagnosticoRepository.save(diagnostico));
    }

    @Override
    public DiagnosticoResponseDTO update(UUID id, DiagnosticoRequestDTO request) {
        Diagnostico diagnostico = findEntity(id);
        diagnosticoMapper.updateEntity(request, diagnostico);
        return diagnosticoMapper.toResponse(diagnosticoRepository.save(diagnostico));
    }

    @Override
    public void delete(UUID id) {
        diagnosticoRepository.delete(findEntity(id));
    }

    private Diagnostico findEntity(UUID id) {
        return diagnosticoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Diagnostico no encontrado con id: " + id));
    }
}
