package com.vetsync.app.controller;

import com.vetsync.app.dto.request.PlanSanitarioRequest;
import com.vetsync.app.entity.PlanSanitario;
import com.vetsync.app.service.impl.PlanSanitarioService;
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
@RequestMapping("/planes")
@RequiredArgsConstructor
@Tag(name = "Planes Sanitarios", description = "Gestión de vacunación y planes sanitarios de mascotas")
@SecurityRequirement(name = "bearerAuth")
public class PlanSanitarioController {

    private final PlanSanitarioService planSanitarioService;

    @GetMapping
    @Operation(summary = "Listar todos los planes sanitarios")
    public ResponseEntity<List<PlanSanitario>> findAll() {
        return ResponseEntity.ok(planSanitarioService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener plan sanitario por ID")
    public ResponseEntity<PlanSanitario> findById(@PathVariable Long id) {
        return ResponseEntity.ok(planSanitarioService.findById(id));
    }

    @GetMapping("/mascota/{mascotaId}")
    @Operation(summary = "Listar planes sanitarios de una mascota")
    public ResponseEntity<List<PlanSanitario>> findByMascotaId(@PathVariable Long mascotaId) {
        return ResponseEntity.ok(planSanitarioService.findByMascotaId(mascotaId));
    }

    @PostMapping
    @Operation(summary = "Registrar nuevo plan sanitario / vacuna")
    public ResponseEntity<PlanSanitario> create(@Valid @RequestBody PlanSanitarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(planSanitarioService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar plan sanitario")
    public ResponseEntity<PlanSanitario> update(@PathVariable Long id,
                                                 @Valid @RequestBody PlanSanitarioRequest request) {
        return ResponseEntity.ok(planSanitarioService.update(id, request));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado de vacuna (VIGENTE/VENCIDA/PENDIENTE)")
    public ResponseEntity<PlanSanitario> actualizarEstado(@PathVariable Long id,
                                                           @RequestParam PlanSanitario.EstadoVacuna estado) {
        return ResponseEntity.ok(planSanitarioService.actualizarEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar plan sanitario")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        planSanitarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
