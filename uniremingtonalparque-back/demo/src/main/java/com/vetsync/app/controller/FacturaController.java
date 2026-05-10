package com.vetsync.app.controller;

import com.vetsync.app.dto.request.FacturaRequest;
import com.vetsync.app.entity.Factura;
import com.vetsync.app.service.impl.FacturaService;
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
@RequestMapping("/facturas")
@RequiredArgsConstructor
@Tag(name = "Facturación", description = "Gestión de facturas y estado de pago")
@SecurityRequirement(name = "bearerAuth")
public class FacturaController {

    private final FacturaService facturaService;

    @GetMapping
    @Operation(summary = "Listar todas las facturas")
    public ResponseEntity<List<Factura>> findAll() {
        return ResponseEntity.ok(facturaService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener factura por ID")
    public ResponseEntity<Factura> findById(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.findById(id));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar facturas de un cliente")
    public ResponseEntity<List<Factura>> findByClienteId(@PathVariable Long clienteId) {
        return ResponseEntity.ok(facturaService.findByClienteId(clienteId));
    }

    @PostMapping
    @Operation(summary = "Crear nueva factura")
    public ResponseEntity<Factura> create(@Valid @RequestBody FacturaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facturaService.create(request));
    }

    @PatchMapping("/{id}/pagar")
    @Operation(summary = "Marcar factura como pagada")
    public ResponseEntity<Factura> marcarPagada(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.marcarPagada(id));
    }

    @PatchMapping("/{id}/anular")
    @Operation(summary = "Anular factura")
    public ResponseEntity<Factura> anular(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.anular(id));
    }
}
