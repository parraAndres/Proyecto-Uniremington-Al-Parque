package com.vetsync.app.controller;

import com.vetsync.app.dto.request.HistorialPagoRequest;
import com.vetsync.app.entity.HistorialPago;
import com.vetsync.app.service.impl.HistorialPagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/pagos")
@RequiredArgsConstructor
@Tag(name = "Historial de Pagos", description = "Registro de pagos asociados a facturas")
@SecurityRequirement(name = "bearerAuth")
public class HistorialPagoController {

    private final HistorialPagoService historialPagoService;

    @GetMapping("/factura/{facturaId}")
    @Operation(summary = "Listar pagos de una factura")
    public ResponseEntity<List<HistorialPago>> findByFacturaId(@PathVariable Long facturaId) {
        return ResponseEntity.ok(historialPagoService.findByFacturaId(facturaId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pago por ID")
    public ResponseEntity<HistorialPago> findById(@PathVariable Long id) {
        return ResponseEntity.ok(historialPagoService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Registrar pago de factura")
    public ResponseEntity<HistorialPago> registrarPago(@Valid @RequestBody HistorialPagoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(historialPagoService.registrarPago(request));
    }
}
