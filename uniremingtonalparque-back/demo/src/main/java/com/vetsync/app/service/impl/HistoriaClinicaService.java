package com.vetsync.app.service.impl;

import com.vetsync.app.dto.request.HistoriaClinicaRequest;
import com.vetsync.app.entity.Cita;
import com.vetsync.app.entity.HistoriaClinica;
import com.vetsync.app.exception.RecursoNotFoundException;
import com.vetsync.app.exception.ReglaDeNegocioException;
import com.vetsync.app.repository.CitaRepository;
import com.vetsync.app.repository.HistoriaClinicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoriaClinicaService {

    private final HistoriaClinicaRepository historiaClinicaRepository;
    private final CitaRepository citaRepository;

    public List<HistoriaClinica> findAll() {
        return historiaClinicaRepository.findAll();
    }

    public HistoriaClinica findById(Long id) {
        return historiaClinicaRepository.findById(id)
                .orElseThrow(() -> new RecursoNotFoundException("Historia clínica no encontrada con id: " + id));
    }

    public HistoriaClinica findByCitaId(Long citaId) {
        return historiaClinicaRepository.findByCitaId(citaId)
                .orElseThrow(() -> new RecursoNotFoundException("No hay historia clínica para la cita id: " + citaId));
    }

    public List<HistoriaClinica> findByMascotaId(Long mascotaId) {
        return historiaClinicaRepository.findByCita_MascotaId(mascotaId);
    }

    @Transactional
    public HistoriaClinica create(HistoriaClinicaRequest request) {
        Cita cita = citaRepository.findById(request.getCitaId())
                .orElseThrow(() -> new RecursoNotFoundException("Cita no encontrada con id: " + request.getCitaId()));

        if (historiaClinicaRepository.findByCitaId(request.getCitaId()).isPresent()) {
            throw new ReglaDeNegocioException("Ya existe una historia clínica para la cita id: " + request.getCitaId());
        }

        // Marcar la cita como EN_CURSO si estaba PROGRAMADA
        if (cita.getEstado() == Cita.EstadoCita.PROGRAMADA) {
            cita.setEstado(Cita.EstadoCita.EN_CURSO);
            citaRepository.save(cita);
        }

        HistoriaClinica historia = HistoriaClinica.builder()
                .cita(cita)
                .anamnesis(request.getAnamnesis())
                .diagnostico(request.getDiagnostico())
                .tratamiento(request.getTratamiento())
                .fechaCreacion(LocalDateTime.now())
                .build();

        return historiaClinicaRepository.save(historia);
    }

    @Transactional
    public HistoriaClinica update(Long id, HistoriaClinicaRequest request) {
        HistoriaClinica historia = findById(id);
        historia.setAnamnesis(request.getAnamnesis());
        historia.setDiagnostico(request.getDiagnostico());
        historia.setTratamiento(request.getTratamiento());
        return historiaClinicaRepository.save(historia);
    }

    @Transactional
    public void delete(Long id) {
        HistoriaClinica historia = findById(id);
        historiaClinicaRepository.delete(historia);
    }
}
