package com.vetsync.app.controller;

import com.vetsync.app.dto.request.MascotaRequest;
import com.vetsync.app.entity.Mascota;
import com.vetsync.app.service.impl.MascotaService;
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
@RequestMapping("/mascotas")
@RequiredArgsConstructor
@Tag(name = "Mascotas", description = "CRUD de mascotas (pacientes)")
@SecurityRequirement(name = "bearerAuth")
public class MascotaController {

    private final MascotaService mascotaService;

    @GetMapping
    @Operation(summary = "Listar todas las mascotas")
    public ResponseEntity<List<Mascota>> findAll() {
        return ResponseEntity.ok(mascotaService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener mascota por ID")
    public ResponseEntity<Mascota> findById(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.findById(id));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar mascotas de un cliente")
    public ResponseEntity<List<Mascota>> findByClienteId(@PathVariable Long clienteId) {
        return ResponseEntity.ok(mascotaService.findByClienteId(clienteId));
    }

    @PostMapping
    @Operation(summary = "Registrar nueva mascota")
    public ResponseEntity<Mascota> create(@Valid @RequestBody MascotaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mascotaService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar mascota")
    public ResponseEntity<Mascota> update(@PathVariable Long id, @Valid @RequestBody MascotaRequest request) {
        return ResponseEntity.ok(mascotaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar mascota")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mascotaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
