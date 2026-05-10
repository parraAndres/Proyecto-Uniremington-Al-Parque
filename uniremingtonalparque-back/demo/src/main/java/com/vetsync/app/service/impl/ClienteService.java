package com.vetsync.app.service.impl;

import com.vetsync.app.dto.request.ClienteRequest;
import com.vetsync.app.entity.Cliente;
import com.vetsync.app.exception.RecursoNotFoundException;
import com.vetsync.app.exception.ReglaDeNegocioException;
import com.vetsync.app.mapper.ClienteMapper;
import com.vetsync.app.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Cliente findById(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNotFoundException("Cliente no encontrado con id: " + id));
    }

    @Transactional
    public Cliente create(ClienteRequest request) {
        if (clienteRepository.existsByDocumento(request.getDocumento())) {
            throw new ReglaDeNegocioException("Ya existe un cliente con el documento: " + request.getDocumento());
        }
        Cliente cliente = clienteMapper.toEntity(request);
        cliente.setFechaRegistro(LocalDate.now());
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente update(Long id, ClienteRequest request) {
        Cliente cliente = findById(id);
        clienteMapper.updateEntity(request, cliente);
        return clienteRepository.save(cliente);
    }

    @Transactional
    public void delete(Long id) {
        Cliente cliente = findById(id);
        clienteRepository.delete(cliente);
    }
}
