package com.vetsync.app.uniremington.controller;

import com.vetsync.app.uniremington.dto.sync.SyncDtos.*;
import com.vetsync.app.uniremington.service.UniSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de sincronización para el módulo "Uniremington al Parque".
 *
 * Cada endpoint recibe un arreglo de registros (creados offline en el frontend)
 * y los persiste usando la estrategia UPSERT (idempotente por UUID del frontend).
 *
 * Todas las rutas requieren Bearer JWT válido en el header Authorization.
 *
 * Rutas disponibles:
 *   POST /api/sync/beneficiaries   → Sincronizar beneficiarios
 *   POST /api/sync/servicios       → Sincronizar servicios prestados
 *   POST /api/sync/seguimientos    → Sincronizar seguimientos de caso
 *   POST /api/sync/diagnosticos    → Sincronizar diagnósticos
 *   POST /api/sync/academico       → Sincronizar participaciones académicas
 *   POST /api/sync/recursos        → Sincronizar recursos/aportes
 */
@RestController
@RequestMapping("/sync")
@RequiredArgsConstructor
@Tag(name = "Sync - Uniremington", description = "Endpoints de sincronización offline-first para la PWA")
@SecurityRequirement(name = "bearerAuth")
public class UniSyncController {

    private final UniSyncService syncService;

    // ────────────────────────────────────────────────────────────────
    //  BENEFICIARIOS
    // ────────────────────────────────────────────────────────────────

    @PostMapping("/beneficiaries")
    @Operation(
        summary = "Sincronizar beneficiarios",
        description = "Recibe un arreglo de beneficiarios creados offline y los persiste (UPSERT por UUID)"
    )
    public ResponseEntity<List<SyncItemResult>> syncBeneficiaries(
            @Valid @RequestBody List<BeneficiarioDto> beneficiarios) {
        return ResponseEntity.ok(syncService.syncBeneficiarios(beneficiarios));
    }

    // ────────────────────────────────────────────────────────────────
    //  SERVICIOS PRESTADOS
    // ────────────────────────────────────────────────────────────────

    @PostMapping("/servicios")
    @Operation(
        summary = "Sincronizar servicios prestados",
        description = "Recibe un arreglo de servicios prestados y los persiste (UPSERT por UUID)"
    )
    public ResponseEntity<List<SyncItemResult>> syncServicios(
            @Valid @RequestBody List<ServicioDto> servicios) {
        return ResponseEntity.ok(syncService.syncServicios(servicios));
    }

    // ────────────────────────────────────────────────────────────────
    //  SEGUIMIENTOS
    // ────────────────────────────────────────────────────────────────

    @PostMapping("/seguimientos")
    @Operation(
        summary = "Sincronizar seguimientos de caso",
        description = "Recibe un arreglo de seguimientos y los persiste (UPSERT por UUID)"
    )
    public ResponseEntity<List<SyncItemResult>> syncSeguimientos(
            @Valid @RequestBody List<SeguimientoDto> seguimientos) {
        return ResponseEntity.ok(syncService.syncSeguimientos(seguimientos));
    }

    // ────────────────────────────────────────────────────────────────
    //  DIAGNÓSTICOS
    // ────────────────────────────────────────────────────────────────

    @PostMapping("/diagnosticos")
    @Operation(
        summary = "Sincronizar diagnósticos",
        description = "Recibe un arreglo de diagnósticos y los persiste (UPSERT por UUID)"
    )
    public ResponseEntity<List<SyncItemResult>> syncDiagnosticos(
            @Valid @RequestBody List<DiagnosticoDto> diagnosticos) {
        return ResponseEntity.ok(syncService.syncDiagnosticos(diagnosticos));
    }

    // ────────────────────────────────────────────────────────────────
    //  ACADÉMICO
    // ────────────────────────────────────────────────────────────────

    @PostMapping("/academico")
    @Operation(
        summary = "Sincronizar participaciones académicas",
        description = "Recibe un arreglo de participaciones académicas y los persiste (UPSERT por UUID)"
    )
    public ResponseEntity<List<SyncItemResult>> syncAcademico(
            @Valid @RequestBody List<AcademicoDto> academico) {
        return ResponseEntity.ok(syncService.syncAcademico(academico));
    }

    // ────────────────────────────────────────────────────────────────
    //  RECURSOS / APORTES
    // ────────────────────────────────────────────────────────────────

    @PostMapping("/recursos")
    @Operation(
        summary = "Sincronizar recursos y aportes",
        description = "Recibe un arreglo de recursos o aportes y los persiste (UPSERT por UUID)"
    )
    public ResponseEntity<List<SyncItemResult>> syncRecursos(
            @Valid @RequestBody List<RecursoDto> recursos) {
        return ResponseEntity.ok(syncService.syncRecursos(recursos));
    }
}
