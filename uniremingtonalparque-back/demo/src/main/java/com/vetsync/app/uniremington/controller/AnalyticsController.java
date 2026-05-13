package com.vetsync.app.uniremington.controller;

import com.vetsync.app.uniremington.dto.AnalyticsDtos;
import com.vetsync.app.uniremington.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/uni/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/impact")
    public ResponseEntity<AnalyticsDtos.ImpactStats> getImpactStats() {
        return ResponseEntity.ok(analyticsService.getImpactStats());
    }

    @GetMapping("/facultades")
    public ResponseEntity<List<AnalyticsDtos.FacultadStats>> getFacultadStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(analyticsService.estadisticasPorFacultad(inicio, fin));
    }

    @GetMapping("/territorial")
    public ResponseEntity<List<AnalyticsDtos.ProblematicaStats>> getTerritorialStats() {
        return ResponseEntity.ok(analyticsService.problematicasTerritoriales());
    }

    @GetMapping("/efficiency")
    public ResponseEntity<Map<String, Object>> getEfficiencyMetrics() {
        return ResponseEntity.ok(analyticsService.getEfficiencyMetrics());
    }

    @GetMapping("/ranking-estudiantes")
    public ResponseEntity<List<Map<String, Object>>> getRankingEstudiantes() {
        return ResponseEntity.ok(analyticsService.getRankingEstudiantes());
    }

    @GetMapping("/casos")
    public ResponseEntity<Map<String, Object>> getCasosStats() {
        return ResponseEntity.ok(analyticsService.getCasosStats());
    }

    @GetMapping("/resumen-estrategico")
    public ResponseEntity<Map<String, Object>> getResumenEstrategico() {
        return ResponseEntity.ok(analyticsService.getResumenEstrategico());
    }

    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> getResumenGeneral(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(analyticsService.resumenGeneral(inicio, fin));
    }
}
