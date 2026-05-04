package com.vetsync.app.controller;

import com.vetsync.app.dto.request.CitaRequest;
import com.vetsync.app.entity.Cita;
import com.vetsync.app.service.impl.CitaService;
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
@RequestMapping("/citas")
@RequiredArgsConstructor
@Tag(name = "Citas", description = "Gestión de citas veterinarias")
@SecurityRequirement(name = "bearerAuth")
public class CitaController {

    private final CitaService citaService;

    @GetMapping
    @Operation(summary = "Listar todas las citas")
    public ResponseEntity<List<Cita>> findAll() {
        return ResponseEntity.ok(citaService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cita por ID")
    public ResponseEntity<Cita> findById(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.findById(id));
    }

    @GetMapping("/mascota/{mascotaId}")
    @Operation(summary = "Listar citas de una mascota")
    public ResponseEntity<List<Cita>> findByMascotaId(@PathVariable Long mascotaId) {
        return ResponseEntity.ok(citaService.findByMascotaId(mascotaId));
    }

    @GetMapping("/veterinario/{veterinarioId}")
    @Operation(summary = "Listar citas de un veterinario")
    public ResponseEntity<List<Cita>> findByVeterinarioId(@PathVariable Long veterinarioId) {
        return ResponseEntity.ok(citaService.findByVeterinarioId(veterinarioId));
    }

    @PostMapping
    @Operation(summary = "Crear nueva cita")
    public ResponseEntity<Cita> create(@Valid @RequestBody CitaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(citaService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cita")
    public ResponseEntity<Cita> update(@PathVariable Long id, @Valid @RequestBody CitaRequest request) {
        return ResponseEntity.ok(citaService.update(id, request));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado de la cita")
    public ResponseEntity<Cita> updateEstado(@PathVariable Long id,
                                              @RequestParam Cita.EstadoCita estado) {
        return ResponseEntity.ok(citaService.updateEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cita")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        citaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
