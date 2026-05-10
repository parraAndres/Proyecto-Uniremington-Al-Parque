package com.vetsync.app.controller;

import com.vetsync.app.dto.request.HistoriaClinicaRequest;
import com.vetsync.app.entity.HistoriaClinica;
import com.vetsync.app.service.impl.HistoriaClinicaService;
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
@RequestMapping("/historias")
@RequiredArgsConstructor
@Tag(name = "Historias Clínicas", description = "Gestión de historias clínicas vinculadas a citas")
@SecurityRequirement(name = "bearerAuth")
public class HistoriaClinicaController {

    private final HistoriaClinicaService historiaClinicaService;

    @GetMapping
    @Operation(summary = "Listar todas las historias clínicas")
    public ResponseEntity<List<HistoriaClinica>> findAll() {
        return ResponseEntity.ok(historiaClinicaService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener historia clínica por ID")
    public ResponseEntity<HistoriaClinica> findById(@PathVariable Long id) {
        return ResponseEntity.ok(historiaClinicaService.findById(id));
    }

    @GetMapping("/cita/{citaId}")
    @Operation(summary = "Obtener historia clínica por ID de cita")
    public ResponseEntity<HistoriaClinica> findByCitaId(@PathVariable Long citaId) {
        return ResponseEntity.ok(historiaClinicaService.findByCitaId(citaId));
    }

    @GetMapping("/mascota/{mascotaId}")
    @Operation(summary = "Listar historias clínicas de una mascota")
    public ResponseEntity<List<HistoriaClinica>> findByMascotaId(@PathVariable Long mascotaId) {
        return ResponseEntity.ok(historiaClinicaService.findByMascotaId(mascotaId));
    }

    @PostMapping
    @Operation(summary = "Crear historia clínica para una cita")
    public ResponseEntity<HistoriaClinica> create(@Valid @RequestBody HistoriaClinicaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(historiaClinicaService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar historia clínica")
    public ResponseEntity<HistoriaClinica> update(@PathVariable Long id,
                                                   @Valid @RequestBody HistoriaClinicaRequest request) {
        return ResponseEntity.ok(historiaClinicaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar historia clínica")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        historiaClinicaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
