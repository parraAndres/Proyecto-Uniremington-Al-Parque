package com.vetsync.app.service.impl;

import com.vetsync.app.dto.request.FacturaRequest;
import com.vetsync.app.entity.Cita;
import com.vetsync.app.entity.Cliente;
import com.vetsync.app.entity.Factura;
import com.vetsync.app.exception.RecursoNotFoundException;
import com.vetsync.app.exception.ReglaDeNegocioException;
import com.vetsync.app.repository.CitaRepository;
import com.vetsync.app.repository.ClienteRepository;
import com.vetsync.app.repository.FacturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final ClienteRepository clienteRepository;
    private final CitaRepository citaRepository;

    public List<Factura> findAll() {
        return facturaRepository.findAll();
    }

    public Factura findById(Long id) {
        return facturaRepository.findById(id)
                .orElseThrow(() -> new RecursoNotFoundException("Factura no encontrada con id: " + id));
    }

    public List<Factura> findByClienteId(Long clienteId) {
        return facturaRepository.findByClienteId(clienteId);
    }

    @Transactional
    public Factura create(FacturaRequest request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RecursoNotFoundException("Cliente no encontrado con id: " + request.getClienteId()));

        Factura.FacturaBuilder builder = Factura.builder()
                .numero(generarNumeroFactura())
                .cliente(cliente)
                .subtotal(request.getSubtotal())
                .impuesto(request.getImpuesto())
                .total(request.getTotal())
                .fechaEmision(request.getFechaEmision())
                .estado(Factura.EstadoFactura.PENDIENTE)
                .creadoEn(LocalDateTime.now());

        if (request.getCitaId() != null) {
            Cita cita = citaRepository.findById(request.getCitaId())
                    .orElseThrow(() -> new RecursoNotFoundException("Cita no encontrada con id: " + request.getCitaId()));
            builder.cita(cita);
        }

        return facturaRepository.save(builder.build());
    }

    @Transactional
    public Factura anular(Long id) {
        Factura factura = findById(id);
        if (factura.getEstado() == Factura.EstadoFactura.ANULADA) {
            throw new ReglaDeNegocioException("La factura ya está anulada");
        }
        factura.setEstado(Factura.EstadoFactura.ANULADA);
        return facturaRepository.save(factura);
    }

    @Transactional
    public Factura marcarPagada(Long id) {
        Factura factura = findById(id);
        if (factura.getEstado() != Factura.EstadoFactura.PENDIENTE) {
            throw new ReglaDeNegocioException("Solo se pueden marcar como pagadas las facturas PENDIENTE");
        }
        factura.setEstado(Factura.EstadoFactura.PAGADA);
        return facturaRepository.save(factura);
    }

    private String generarNumeroFactura() {
        String prefijo = "FAC-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + "-";
        long count = facturaRepository.count() + 1;
        return prefijo + String.format("%04d", count);
    }
}
