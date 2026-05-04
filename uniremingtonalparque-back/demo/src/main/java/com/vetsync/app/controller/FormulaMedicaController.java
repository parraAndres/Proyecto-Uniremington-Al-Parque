package com.vetsync.app.controller;

import com.vetsync.app.dto.request.FormulaMedicaRequest;
import com.vetsync.app.entity.FormulaMedica;
import com.vetsync.app.service.impl.FormulaMedicaService;
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
@RequestMapping("/formulas")
@RequiredArgsConstructor
@Tag(name = "Fórmulas Médicas", description = "Emisión, validación y dispensación de fórmulas médicas")
@SecurityRequirement(name = "bearerAuth")
public class FormulaMedicaController {

    private final FormulaMedicaService formulaMedicaService;

    @GetMapping
    @Operation(summary = "Listar todas las fórmulas médicas")
    public ResponseEntity<List<FormulaMedica>> findAll() {
        return ResponseEntity.ok(formulaMedicaService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener fórmula médica por ID")
    public ResponseEntity<FormulaMedica> findById(@PathVariable Long id) {
        return ResponseEntity.ok(formulaMedicaService.findById(id));
    }

    @GetMapping("/pendientes")
    @Operation(summary = "Listar fórmulas pendientes de dispensar")
    public ResponseEntity<List<FormulaMedica>> findPendientes() {
        return ResponseEntity.ok(formulaMedicaService.findPendientes());
    }

    @PostMapping
    @Operation(summary = "Emitir nueva fórmula médica")
    public ResponseEntity<FormulaMedica> create(@Valid @RequestBody FormulaMedicaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(formulaMedicaService.create(request));
    }

    @PatchMapping("/{id}/validar")
    @Operation(summary = "Validar fórmula médica")
    public ResponseEntity<FormulaMedica> validar(@PathVariable Long id) {
        return ResponseEntity.ok(formulaMedicaService.validar(id));
    }

    @PatchMapping("/{id}/dispensar")
    @Operation(summary = "Dispensar fórmula médica")
    public ResponseEntity<FormulaMedica> dispensar(@PathVariable Long id,
                                                    @RequestParam Long farmaceuticoId) {
        return ResponseEntity.ok(formulaMedicaService.dispensar(id, farmaceuticoId));
    }

    @PatchMapping("/{id}/rechazar")
    @Operation(summary = "Rechazar fórmula médica")
    public ResponseEntity<FormulaMedica> rechazar(@PathVariable Long id) {
        return ResponseEntity.ok(formulaMedicaService.rechazar(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar fórmula médica")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        formulaMedicaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
