package com.vetsync.app.controller;

import com.vetsync.app.dto.request.SincronizacionBatchRequest;
import com.vetsync.app.dto.response.SincronizacionResponse;
import com.vetsync.app.service.impl.SincronizacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("vetsyncSincronizacionController") // ← nombre de bean único
@RequestMapping("/api/vetsync/sincronizacion")     // ← ruta diferente para evitar conflicto
@RequiredArgsConstructor
@Tag(name = "Sincronización VetSync", description = "Motor de ingesta masiva offline-first con idempotencia")
@SecurityRequirement(name = "bearerAuth")
public class SincronizacionController {

    private final SincronizacionService sincronizacionService;

    @PostMapping("/batch")
    @Operation(
        summary = "Procesar lote de datos offline",
        description = "Ingesta masiva con clave de idempotencia UUID. " +
                      "Estrategia Last-Write-Wins para deduplicación. " +
                      "Auditoría del estado en tabla lotes_sincronizacion."
    )
    public ResponseEntity<SincronizacionResponse> procesarBatch(
            @Valid @RequestBody SincronizacionBatchRequest request) {
        return ResponseEntity.ok(sincronizacionService.procesarLote(request));
    }
}