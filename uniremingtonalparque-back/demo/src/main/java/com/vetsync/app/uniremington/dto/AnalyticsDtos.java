package com.vetsync.app.uniremington.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class AnalyticsDtos {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImpactStats {
        private long personasRegistradas;
        private long personasActivas;
        private long municipiosVisitados;
        private long personasAtendidas; // Beneficiarios únicos
        private long totalAsistencias;  // Total servicios
        private long totalEstudiantes;
        private BigDecimal inversionSocialEstimada;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FacultadStats {
        private String facultad;
        private long totalAtenciones;
        private long beneficiariosUnicos;
        private BigDecimal horasAcademicas;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CoberturaTerritorialStats {
        private String municipio;
        private long beneficiariosUnicos;
        private long totalAtenciones;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProblematicaStats {
        private String problematica;
        private long total;
        private String zonaMasAfectada;
    }
}
