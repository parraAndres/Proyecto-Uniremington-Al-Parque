package com.vetsync.app.uniremington.service;

import com.vetsync.app.uniremington.dto.AnalyticsDtos;
import com.vetsync.app.uniremington.repository.BeneficiarioRepository;
import com.vetsync.app.uniremington.repository.ParticipacionAcademicaRepository;
import com.vetsync.app.uniremington.repository.ServicioSocialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ServicioSocialRepository servicioSocialRepository;
    private final ParticipacionAcademicaRepository participacionAcademicaRepository;
    private final BeneficiarioRepository beneficiarioRepository;
    private final com.vetsync.app.uniremington.repository.UsuarioUniremingtonRepository usuarioUniremingtonRepository;

    public List<AnalyticsDtos.FacultadStats> estadisticasPorFacultad(LocalDateTime inicio, LocalDateTime fin) {
        Map<String, AnalyticsDtos.FacultadStats.FacultadStatsBuilder> acumulado = new HashMap<>();

        for (Object[] row : servicioSocialRepository.resumenPorFacultad(inicio, fin)) {
            String facultad = Objects.toString(row[0], "SIN_FACULTAD");
            long atenciones = ((Number) row[1]).longValue();
            long beneficiarios = ((Number) row[2]).longValue();
            acumulado.put(facultad, AnalyticsDtos.FacultadStats.builder()
                    .facultad(facultad)
                    .totalAtenciones(atenciones)
                    .beneficiariosUnicos(beneficiarios)
                    .horasAcademicas(BigDecimal.ZERO));
        }

        LocalDate inicioDate = inicio != null ? inicio.toLocalDate() : null;
        LocalDate finDate = fin != null ? fin.toLocalDate() : null;
        for (Object[] row : participacionAcademicaRepository.horasPorFacultad(inicioDate, finDate)) {
            String facultad = Objects.toString(row[0], "SIN_FACULTAD");
            BigDecimal horas = (BigDecimal) row[1];
            AnalyticsDtos.FacultadStats.FacultadStatsBuilder base = acumulado.getOrDefault(
                    facultad,
                    AnalyticsDtos.FacultadStats.builder()
                            .facultad(facultad)
                            .totalAtenciones(0)
                            .beneficiariosUnicos(0)
                            .horasAcademicas(BigDecimal.ZERO)
            );
            acumulado.put(facultad, base.horasAcademicas(horas));
        }

        return acumulado.values().stream().map(AnalyticsDtos.FacultadStats.FacultadStatsBuilder::build).toList();
    }

    public List<AnalyticsDtos.CoberturaTerritorialStats> coberturaTerritorial() {
        Map<String, AnalyticsDtos.CoberturaTerritorialStats> salida = new HashMap<>();
        beneficiarioRepository.findAll().forEach(beneficiario -> salida.compute(beneficiario.getMunicipio(), (k, v) ->
                v == null
                        ? AnalyticsDtos.CoberturaTerritorialStats.builder()
                        .municipio(k)
                        .beneficiariosUnicos(1)
                        .totalAtenciones(0)
                        .build()
                        : AnalyticsDtos.CoberturaTerritorialStats.builder()
                        .municipio(k)
                        .beneficiariosUnicos(v.getBeneficiariosUnicos() + 1)
                        .totalAtenciones(v.getTotalAtenciones())
                        .build()));
        servicioSocialRepository.findAll().forEach(servicio -> {
            String municipio = servicio.getBeneficiario().getMunicipio();
            AnalyticsDtos.CoberturaTerritorialStats actual = salida.get(municipio);
            if (actual == null) {
                salida.put(municipio, AnalyticsDtos.CoberturaTerritorialStats.builder()
                        .municipio(municipio)
                        .beneficiariosUnicos(0)
                        .totalAtenciones(1)
                        .build());
                return;
            }
            salida.put(municipio, AnalyticsDtos.CoberturaTerritorialStats.builder()
                    .municipio(municipio)
                    .beneficiariosUnicos(actual.getBeneficiariosUnicos())
                    .totalAtenciones(actual.getTotalAtenciones() + 1)
                    .build());
        });
        return new ArrayList<>(salida.values());
    }

    public List<AnalyticsDtos.ProblematicaStats> problematicasFrecuentes(LocalDateTime inicio, LocalDateTime fin) {
        return servicioSocialRepository.problematicasFrecuentes(inicio, fin).stream()
                .map(row -> AnalyticsDtos.ProblematicaStats.builder()
                        .problematica(Objects.toString(row[0], "SIN_TIPO"))
                        .total(((Number) row[1]).longValue())
                        .build())
                .toList();
    }

    public Map<String, Object> resumenGeneral(LocalDateTime inicio, LocalDateTime fin) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("facultades", estadisticasPorFacultad(inicio, fin));
        out.put("problematicasFrecuentes", problematicasFrecuentes(inicio, fin));
        out.put("coberturaTerritorial", coberturaTerritorial());
        return out;
    }

    public AnalyticsDtos.ImpactStats getImpactStats() {
        long municipiosVisitados = coberturaTerritorial().size();
        long personasAtendidas = beneficiarioRepository.count(); // Beneficiarios únicos
        long personasRegistradas = usuarioUniremingtonRepository.count();
        long personasActivas = usuarioUniremingtonRepository.countByUpdatedAtAfter(LocalDateTime.now().minusDays(7));
        long totalAsistencias = servicioSocialRepository.count();
        long totalEstudiantes = usuarioUniremingtonRepository.countByRol("ESTUDIANTE");

        return AnalyticsDtos.ImpactStats.builder()
                .municipiosVisitados(municipiosVisitados)
                .personasAtendidas(personasAtendidas)
                .personasRegistradas(personasRegistradas)
                .personasActivas(personasActivas)
                .totalAsistencias(totalAsistencias)
                .totalEstudiantes(totalEstudiantes)
                .build();
    }
}
