package com.uniremington.alparque.controller;

import com.uniremington.alparque.dto.request.SincronizacionBatchRequestDTO;
import com.uniremington.alparque.dto.response.SincronizacionLotePageResponseDTO;
import com.uniremington.alparque.dto.response.SincronizacionResponseDTO;
import com.uniremington.alparque.service.SincronizacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController("alparqueSincronizacionController") // ← nombre de bean único
@RequestMapping("/api/sincronizacion")
@RequiredArgsConstructor
@Tag(name = "Sincronización al Parque", description = "Sincronización offline-first por lotes con idempotencia")
@SecurityRequirement(name = "bearerAuth")
public class SincronizacionController {

    private final SincronizacionService sincronizacionService;

    @PostMapping("/batch")
    @Operation(
        summary = "Procesar lote de sincronización",
        description = "Recibe un lote de registros offline y los persiste usando idempotencia por clave única"
    )
    public ResponseEntity<SincronizacionResponseDTO> sincronizarBatch(
            @Valid @RequestBody SincronizacionBatchRequestDTO request) {
        return ResponseEntity.ok(sincronizacionService.sincronizarBatch(request));
    }

    @GetMapping("/lotes/recientes")
    @Operation(
        summary = "Lotes recientes",
        description = "Devuelve los lotes más recientes de un dispositivo, opcionalmente filtrados por estado"
    )
    public ResponseEntity<SincronizacionLotePageResponseDTO> lotesRecientes(
            @RequestParam(required = false) String dispositivoId,
            @RequestParam(required = false) String estadoLote,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                sincronizacionService.listarLotesRecientes(dispositivoId, estadoLote, page, size));
    }

    @GetMapping("/lotes/historial")
    @Operation(
        summary = "Historial de lotes",
        description = "Devuelve el historial de lotes en un rango de fechas"
    )
    public ResponseEntity<SincronizacionLotePageResponseDTO> historialLotes(
            @RequestParam(required = false) String dispositivoId,
            @RequestParam(required = false) String estadoLote,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                sincronizacionService.listarHistorialLotes(
                        dispositivoId, estadoLote, fechaInicio, fechaFin, page, size));
    }
}