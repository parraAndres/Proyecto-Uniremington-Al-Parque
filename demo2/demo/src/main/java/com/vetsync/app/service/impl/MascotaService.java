package com.vetsync.app.service.impl;

import com.vetsync.app.dto.request.MascotaRequest;
import com.vetsync.app.entity.Cliente;
import com.vetsync.app.entity.Mascota;
import com.vetsync.app.exception.RecursoNotFoundException;
import com.vetsync.app.repository.ClienteRepository;
import com.vetsync.app.repository.MascotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final ClienteRepository clienteRepository;

    public List<Mascota> findAll() {
        return mascotaRepository.findAll();
    }

    public List<Mascota> findByClienteId(Long clienteId) {
        return mascotaRepository.findByClienteId(clienteId);
    }

    public Mascota findById(Long id) {
        return mascotaRepository.findById(id)
                .orElseThrow(() -> new RecursoNotFoundException("Mascota no encontrada con id: " + id));
    }

    @Transactional
    public Mascota create(MascotaRequest request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RecursoNotFoundException("Cliente no encontrado con id: " + request.getClienteId()));

        Mascota mascota = Mascota.builder()
                .nombre(request.getNombre())
                .especie(request.getEspecie())
                .raza(request.getRaza())
                .edad(request.getEdad())
                .sexo(request.getSexo())
                .cliente(cliente)
                .fechaRegistro(LocalDate.now())
                .build();

        return mascotaRepository.save(mascota);
    }

    @Transactional
    public Mascota update(Long id, MascotaRequest request) {
        Mascota mascota = findById(id);
        mascota.setNombre(request.getNombre());
        mascota.setEspecie(request.getEspecie());
        mascota.setRaza(request.getRaza());
        mascota.setEdad(request.getEdad());
        mascota.setSexo(request.getSexo());

        if (!mascota.getCliente().getId().equals(request.getClienteId())) {
            Cliente cliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new RecursoNotFoundException("Cliente no encontrado con id: " + request.getClienteId()));
            mascota.setCliente(cliente);
        }

        return mascotaRepository.save(mascota);
    }

    @Transactional
    public void delete(Long id) {
        Mascota mascota = findById(id);
        mascotaRepository.delete(mascota);
    }
}
