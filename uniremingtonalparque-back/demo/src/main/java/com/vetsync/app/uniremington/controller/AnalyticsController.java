package com.vetsync.app.uniremington.controller;

import com.vetsync.app.uniremington.dto.AnalyticsDtos;
import com.vetsync.app.uniremington.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/facultades")
    public List<AnalyticsDtos.FacultadStats> facultades(
            @RequestParam(required = false) LocalDateTime fechaInicio,
            @RequestParam(required = false) LocalDateTime fechaFin) {
        return analyticsService.estadisticasPorFacultad(fechaInicio, fechaFin);
    }

    @GetMapping("/cobertura-territorial")
    public List<AnalyticsDtos.CoberturaTerritorialStats> coberturaTerritorial() {
        return analyticsService.coberturaTerritorial();
    }

    @GetMapping("/problematicas-frecuentes")
    public List<AnalyticsDtos.ProblematicaStats> problematicasFrecuentes(
            @RequestParam(required = false) LocalDateTime fechaInicio,
            @RequestParam(required = false) LocalDateTime fechaFin) {
        return analyticsService.problematicasFrecuentes(fechaInicio, fechaFin);
    }

    @GetMapping("/resumen")
    public Map<String, Object> resumen(
            @RequestParam(required = false) LocalDateTime fechaInicio,
            @RequestParam(required = false) LocalDateTime fechaFin) {
        return analyticsService.resumenGeneral(fechaInicio, fechaFin);
    }

    @GetMapping("/impact")
    public AnalyticsDtos.ImpactStats getImpactStats() {
        return analyticsService.getImpactStats();
    }
}
