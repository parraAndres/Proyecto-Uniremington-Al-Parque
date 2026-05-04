package com.vetsync.app.controller;

import com.vetsync.app.dto.response.DashboardResponse;
import com.vetsync.app.entity.Usuario;
import com.vetsync.app.repository.UsuarioRepository;
import com.vetsync.app.service.impl.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Métricas y KPIs por rol de usuario")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Métricas del administrador")
    public ResponseEntity<DashboardResponse.MetricasAdmin> getAdmin() {
        return ResponseEntity.ok(dashboardService.getMetricasAdmin());
    }

    @GetMapping("/veterinario")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO')")
    @Operation(summary = "Métricas del veterinario autenticado")
    public ResponseEntity<DashboardResponse.MetricasVeterinario> getVeterinario(Authentication auth) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName()).orElseThrow();
        return ResponseEntity.ok(dashboardService.getMetricasVeterinario(usuario.getId()));
    }

    @GetMapping("/farmaceutico")
    @PreAuthorize("hasAnyRole('ADMIN','FARMACEUTICO')")
    @Operation(summary = "Métricas del farmacéutico")
    public ResponseEntity<DashboardResponse.MetricasFarmaceutico> getFarmaceutico() {
        return ResponseEntity.ok(dashboardService.getMetricasFarmaceutico());
    }

    @GetMapping("/auxiliar")
    @PreAuthorize("hasAnyRole('ADMIN','AUXILIAR')")
    @Operation(summary = "Métricas del auxiliar")
    public ResponseEntity<DashboardResponse.MetricasAuxiliar> getAuxiliar() {
        return ResponseEntity.ok(dashboardService.getMetricasAuxiliar());
    }
}
