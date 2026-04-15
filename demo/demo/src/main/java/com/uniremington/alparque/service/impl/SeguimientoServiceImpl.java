package com.uniremington.alparque.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uniremington.alparque.dto.request.SeguimientoRequestDTO;
import com.uniremington.alparque.dto.response.SeguimientoResponseDTO;
import com.uniremington.alparque.exception.ResourceNotFoundException;
import com.uniremington.alparque.mapper.SeguimientoMapper;
import com.uniremington.alparque.model.Caso;
import com.uniremington.alparque.model.Seguimiento;
import com.uniremington.alparque.repository.CasoRepository;
import com.uniremington.alparque.repository.SeguimientoRepository;
import com.uniremington.alparque.service.SeguimientoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SeguimientoServiceImpl implements SeguimientoService {

    private final SeguimientoRepository seguimientoRepository;
    private final CasoRepository casoRepository;
    private final SeguimientoMapper seguimientoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SeguimientoResponseDTO> findAll() {
        return seguimientoRepository.findAll().stream().map(seguimientoMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SeguimientoResponseDTO findById(UUID id) {
        return seguimientoMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeguimientoResponseDTO> findByCasoId(UUID casoId) {
        return seguimientoRepository.findByCasoId(casoId).stream().map(seguimientoMapper::toResponse).toList();
    }

    @Override
    public SeguimientoResponseDTO create(SeguimientoRequestDTO request) {
        Seguimiento seguimiento = seguimientoMapper.toEntity(request);
        Caso caso = casoRepository.findById(request.getCasoId())
            .orElseThrow(() -> new ResourceNotFoundException("Caso no encontrado con id: " + request.getCasoId()));
        seguimiento.setCaso(caso);
        return seguimientoMapper.toResponse(seguimientoRepository.save(seguimiento));
    }

    @Override
    public SeguimientoResponseDTO update(UUID id, SeguimientoRequestDTO request) {
        Seguimiento seguimiento = findEntity(id);
        seguimientoMapper.updateEntity(request, seguimiento);
        Caso caso = casoRepository.findById(request.getCasoId())
            .orElseThrow(() -> new ResourceNotFoundException("Caso no encontrado con id: " + request.getCasoId()));
        seguimiento.setCaso(caso);
        return seguimientoMapper.toResponse(seguimientoRepository.save(seguimiento));
    }

    @Override
    public void delete(UUID id) {
        seguimientoRepository.delete(findEntity(id));
    }

    private Seguimiento findEntity(UUID id) {
        return seguimientoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Seguimiento no encontrado con id: " + id));
    }
}
