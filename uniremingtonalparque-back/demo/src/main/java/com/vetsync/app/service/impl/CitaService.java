package com.vetsync.app.service.impl;

import com.vetsync.app.dto.request.CitaRequest;
import com.vetsync.app.entity.Cita;
import com.vetsync.app.entity.Mascota;
import com.vetsync.app.entity.Usuario;
import com.vetsync.app.exception.RecursoNotFoundException;
import com.vetsync.app.exception.ReglaDeNegocioException;
import com.vetsync.app.repository.CitaRepository;
import com.vetsync.app.repository.MascotaRepository;
import com.vetsync.app.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CitaService {

    private final CitaRepository citaRepository;
    private final MascotaRepository mascotaRepository;
    private final UsuarioRepository usuarioRepository;

    public List<Cita> findAll() {
        return citaRepository.findAll();
    }

    public Cita findById(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNotFoundException("Cita no encontrada con id: " + id));
    }

    public List<Cita> findByMascotaId(Long mascotaId) {
        return citaRepository.findByMascotaId(mascotaId);
    }

    public List<Cita> findByVeterinarioId(Long veterinarioId) {
        return citaRepository.findByVeterinarioId(veterinarioId);
    }

    @Transactional
    public Cita create(CitaRequest request) {
        Mascota mascota = mascotaRepository.findById(request.getMascotaId())
                .orElseThrow(() -> new RecursoNotFoundException("Mascota no encontrada con id: " + request.getMascotaId()));

        Usuario veterinario = usuarioRepository.findById(request.getVeterinarioId())
                .orElseThrow(() -> new RecursoNotFoundException("Veterinario no encontrado con id: " + request.getVeterinarioId()));

        if (veterinario.getRol() != Usuario.Rol.VETERINARIO && veterinario.getRol() != Usuario.Rol.ADMIN) {
            throw new ReglaDeNegocioException("El usuario asignado no tiene rol de VETERINARIO");
        }

        Cita cita = Cita.builder()
                .mascota(mascota)
                .veterinario(veterinario)
                .fechaHora(request.getFechaHora())
                .motivo(request.getMotivo())
                .estado(Cita.EstadoCita.PROGRAMADA)
                .build();

        return citaRepository.save(cita);
    }

    @Transactional
    public Cita updateEstado(Long id, Cita.EstadoCita nuevoEstado) {
        Cita cita = findById(id);
        cita.setEstado(nuevoEstado);
        return citaRepository.save(cita);
    }

    @Transactional
    public Cita update(Long id, CitaRequest request) {
        Cita cita = findById(id);

        Mascota mascota = mascotaRepository.findById(request.getMascotaId())
                .orElseThrow(() -> new RecursoNotFoundException("Mascota no encontrada con id: " + request.getMascotaId()));
        Usuario veterinario = usuarioRepository.findById(request.getVeterinarioId())
                .orElseThrow(() -> new RecursoNotFoundException("Veterinario no encontrado con id: " + request.getVeterinarioId()));

        cita.setMascota(mascota);
        cita.setVeterinario(veterinario);
        cita.setFechaHora(request.getFechaHora());
        cita.setMotivo(request.getMotivo());

        return citaRepository.save(cita);
    }

    @Transactional
    public void delete(Long id) {
        Cita cita = findById(id);
        citaRepository.delete(cita);
    }
}
