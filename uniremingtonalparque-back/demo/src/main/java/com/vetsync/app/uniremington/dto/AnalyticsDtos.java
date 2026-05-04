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
}
