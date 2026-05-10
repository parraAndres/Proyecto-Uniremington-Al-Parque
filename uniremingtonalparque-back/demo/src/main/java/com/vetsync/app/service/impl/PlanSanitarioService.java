package com.vetsync.app.service.impl;

import com.vetsync.app.dto.request.PlanSanitarioRequest;
import com.vetsync.app.entity.Mascota;
import com.vetsync.app.entity.PlanSanitario;
import com.vetsync.app.exception.RecursoNotFoundException;
import com.vetsync.app.repository.MascotaRepository;
import com.vetsync.app.repository.PlanSanitarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanSanitarioService {

    private final PlanSanitarioRepository planSanitarioRepository;
    private final MascotaRepository mascotaRepository;

    public List<PlanSanitario> findAll() {
        return planSanitarioRepository.findAll();
    }

    public PlanSanitario findById(Long id) {
        return planSanitarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNotFoundException("Plan sanitario no encontrado con id: " + id));
    }

    public List<PlanSanitario> findByMascotaId(Long mascotaId) {
        return planSanitarioRepository.findByMascotaId(mascotaId);
    }

    @Transactional
    public PlanSanitario create(PlanSanitarioRequest request) {
        Mascota mascota = mascotaRepository.findById(request.getMascotaId())
                .orElseThrow(() -> new RecursoNotFoundException("Mascota no encontrada con id: " + request.getMascotaId()));

        PlanSanitario plan = PlanSanitario.builder()
                .mascota(mascota)
                .vacuna(request.getVacuna())
                .fechaAplicacion(request.getFechaAplicacion())
                .proximaAplicacion(request.getProximaAplicacion())
                .observaciones(request.getObservaciones())
                .estado(PlanSanitario.EstadoVacuna.VIGENTE)
                .build();

        return planSanitarioRepository.save(plan);
    }

    @Transactional
    public PlanSanitario update(Long id, PlanSanitarioRequest request) {
        PlanSanitario plan = findById(id);
        plan.setVacuna(request.getVacuna());
        plan.setFechaAplicacion(request.getFechaAplicacion());
        plan.setProximaAplicacion(request.getProximaAplicacion());
        plan.setObservaciones(request.getObservaciones());
        return planSanitarioRepository.save(plan);
    }

    @Transactional
    public PlanSanitario actualizarEstado(Long id, PlanSanitario.EstadoVacuna estado) {
        PlanSanitario plan = findById(id);
        plan.setEstado(estado);
        return planSanitarioRepository.save(plan);
    }

    @Transactional
    public void delete(Long id) {
        PlanSanitario plan = findById(id);
        planSanitarioRepository.delete(plan);
    }
}
