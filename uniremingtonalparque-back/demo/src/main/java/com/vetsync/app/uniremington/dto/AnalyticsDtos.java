package com.vetsync.app.uniremington.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

public class AnalyticsDtos {

    @Data
    @Builder
    public static class FacultadStats {
        private String facultad;
        private long totalAtenciones;
        private long beneficiariosUnicos;
        private BigDecimal horasAcademicas;
    }

    @Data
    @Builder
    public static class CoberturaTerritorialStats {
        private String municipio;
        private long beneficiariosUnicos;
        private long totalAtenciones;
    }

    @Data
    @Builder
    public static class ProblematicaStats {
        private String problematica;
        private long total;
    }

    @Data
    @Builder
    public static class ImpactStats {
        private long municipiosVisitados;
        private long personasAtendidas; // Beneficiarios únicos
        private long personasActivas;    // Usuarios activos últimos 7 días
        private long personasRegistradas; // Usuarios totales
        private long totalAsistencias;    // Servicios sociales totales
        private long totalEstudiantes;    // Usuarios con rol ESTUDIANTE
    }
}
