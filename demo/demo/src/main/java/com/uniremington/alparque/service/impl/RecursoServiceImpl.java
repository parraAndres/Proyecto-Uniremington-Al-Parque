package com.uniremington.alparque.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uniremington.alparque.dto.request.RecursoRequestDTO;
import com.uniremington.alparque.dto.response.RecursoResponseDTO;
import com.uniremington.alparque.exception.ResourceNotFoundException;
import com.uniremington.alparque.mapper.RecursoMapper;
import com.uniremington.alparque.model.Recurso;
import com.uniremington.alparque.repository.RecursoRepository;
import com.uniremington.alparque.service.RecursoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RecursoServiceImpl implements RecursoService {

    private final RecursoRepository recursoRepository;
    private final RecursoMapper recursoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RecursoResponseDTO> findAll() {
        return recursoRepository.findAll().stream().map(recursoMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RecursoResponseDTO findById(UUID id) {
        return recursoMapper.toResponse(findEntity(id));
    }

    @Override
    public RecursoResponseDTO create(RecursoRequestDTO request) {
        Recurso recurso = recursoMapper.toEntity(request);
        return recursoMapper.toResponse(recursoRepository.save(recurso));
    }

    @Override
    public RecursoResponseDTO update(UUID id, RecursoRequestDTO request) {
        Recurso recurso = findEntity(id);
        recursoMapper.updateEntity(request, recurso);
        return recursoMapper.toResponse(recursoRepository.save(recurso));
    }

    @Override
    public void delete(UUID id) {
        recursoRepository.delete(findEntity(id));
    }

    private Recurso findEntity(UUID id) {
        return recursoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Recurso no encontrado con id: " + id));
    }
}
