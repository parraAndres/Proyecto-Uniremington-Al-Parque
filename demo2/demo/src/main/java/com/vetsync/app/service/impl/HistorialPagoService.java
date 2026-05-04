package com.vetsync.app.service.impl;

import com.vetsync.app.dto.request.HistorialPagoRequest;
import com.vetsync.app.entity.Factura;
import com.vetsync.app.entity.HistorialPago;
import com.vetsync.app.entity.Usuario;
import com.vetsync.app.exception.RecursoNotFoundException;
import com.vetsync.app.exception.ReglaDeNegocioException;
import com.vetsync.app.repository.FacturaRepository;
import com.vetsync.app.repository.HistorialPagoRepository;
import com.vetsync.app.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistorialPagoService {

    private final HistorialPagoRepository historialPagoRepository;
    private final FacturaRepository facturaRepository;
    private final UsuarioRepository usuarioRepository;

    public List<HistorialPago> findByFacturaId(Long facturaId) {
        return historialPagoRepository.findByFacturaId(facturaId);
    }

    public HistorialPago findById(Long id) {
        return historialPagoRepository.findById(id)
                .orElseThrow(() -> new RecursoNotFoundException("Pago no encontrado con id: " + id));
    }

    @Transactional
    public HistorialPago registrarPago(HistorialPagoRequest request) {
        Factura factura = facturaRepository.findById(request.getFacturaId())
                .orElseThrow(() -> new RecursoNotFoundException("Factura no encontrada con id: " + request.getFacturaId()));

        if (factura.getEstado() == Factura.EstadoFactura.ANULADA) {
            throw new ReglaDeNegocioException("No se puede registrar un pago en una factura anulada");
        }
        if (factura.getEstado() == Factura.EstadoFactura.PAGADA) {
            throw new ReglaDeNegocioException("La factura ya está marcada como pagada");
        }

        Usuario admin = usuarioRepository.findById(request.getAdministradorId())
                .orElseThrow(() -> new RecursoNotFoundException("Administrador no encontrado con id: " + request.getAdministradorId()));

        HistorialPago pago = HistorialPago.builder()
                .factura(factura)
                .administrador(admin)
                .monto(request.getMonto())
                .metodoPago(request.getMetodoPago())
                .observaciones(request.getObservaciones())
                .fechaPago(LocalDateTime.now())
                .build();

        HistorialPago saved = historialPagoRepository.save(pago);

        // Marcar factura como pagada si el monto cubre el total
        if (request.getMonto().compareTo(factura.getTotal()) >= 0) {
            factura.setEstado(Factura.EstadoFactura.PAGADA);
            facturaRepository.save(factura);
        }

        return saved;
    }
}
