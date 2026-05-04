package com.vetsync.app.service.impl;

import com.vetsync.app.dto.request.FormulaMedicaRequest;
import com.vetsync.app.entity.FormulaMedica;
import com.vetsync.app.entity.HistoriaClinica;
import com.vetsync.app.entity.Usuario;
import com.vetsync.app.exception.RecursoNotFoundException;
import com.vetsync.app.exception.ReglaDeNegocioException;
import com.vetsync.app.repository.FormulaMedicaRepository;
import com.vetsync.app.repository.HistoriaClinicaRepository;
import com.vetsync.app.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FormulaMedicaService {

    private final FormulaMedicaRepository formulaRepository;
    private final HistoriaClinicaRepository historiaClinicaRepository;
    private final UsuarioRepository usuarioRepository;

    public List<FormulaMedica> findAll() {
        return formulaRepository.findAll();
    }

    public FormulaMedica findById(Long id) {
        return formulaRepository.findById(id)
                .orElseThrow(() -> new RecursoNotFoundException("Fórmula médica no encontrada con id: " + id));
    }

    public List<FormulaMedica> findPendientes() {
        return formulaRepository.findByEstado(FormulaMedica.EstadoFormula.PENDIENTE);
    }

    @Transactional
    public FormulaMedica create(FormulaMedicaRequest request) {
        HistoriaClinica historia = historiaClinicaRepository.findById(request.getHistoriaClinicaId())
                .orElseThrow(() -> new RecursoNotFoundException("Historia clínica no encontrada con id: " + request.getHistoriaClinicaId()));

        Usuario veterinario = usuarioRepository.findById(request.getVeterinarioId())
                .orElseThrow(() -> new RecursoNotFoundException("Veterinario no encontrado con id: " + request.getVeterinarioId()));

        FormulaMedica formula = FormulaMedica.builder()
                .historiaClinica(historia)
                .veterinario(veterinario)
                .medicamentos(request.getMedicamentos())
                .estado(FormulaMedica.EstadoFormula.PENDIENTE)
                .fechaEmision(LocalDateTime.now())
                .build();

        return formulaRepository.save(formula);
    }

    @Transactional
    public FormulaMedica dispensar(Long id, Long farmaceuticoId) {
        FormulaMedica formula = findById(id);

        if (formula.getEstado() != FormulaMedica.EstadoFormula.PENDIENTE &&
            formula.getEstado() != FormulaMedica.EstadoFormula.VALIDADA) {
            throw new ReglaDeNegocioException("La fórmula no está en estado válido para dispensar. Estado actual: " + formula.getEstado());
        }

        Usuario farmaceutico = usuarioRepository.findById(farmaceuticoId)
                .orElseThrow(() -> new RecursoNotFoundException("Farmacéutico no encontrado con id: " + farmaceuticoId));

        formula.setFarmaceutico(farmaceutico);
        formula.setEstado(FormulaMedica.EstadoFormula.DISPENSADA);
        formula.setFechaDispensacion(LocalDateTime.now());

        return formulaRepository.save(formula);
    }

    @Transactional
    public FormulaMedica validar(Long id) {
        FormulaMedica formula = findById(id);
        if (formula.getEstado() != FormulaMedica.EstadoFormula.PENDIENTE) {
            throw new ReglaDeNegocioException("Solo se pueden validar fórmulas en estado PENDIENTE");
        }
        formula.setEstado(FormulaMedica.EstadoFormula.VALIDADA);
        return formulaRepository.save(formula);
    }

    @Transactional
    public FormulaMedica rechazar(Long id) {
        FormulaMedica formula = findById(id);
        if (formula.getEstado() == FormulaMedica.EstadoFormula.DISPENSADA) {
            throw new ReglaDeNegocioException("No se puede rechazar una fórmula ya dispensada");
        }
        formula.setEstado(FormulaMedica.EstadoFormula.RECHAZADA);
        return formulaRepository.save(formula);
    }

    @Transactional
    public void delete(Long id) {
        FormulaMedica formula = findById(id);
        formulaRepository.delete(formula);
    }
}
