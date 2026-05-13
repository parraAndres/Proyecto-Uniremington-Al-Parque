package com.vetsync.app.uniremington.service;

import com.vetsync.app.uniremington.entity.Jornada;
import com.vetsync.app.uniremington.entity.ServicioSocial;
import com.vetsync.app.uniremington.entity.UsuarioUniremington;
import com.vetsync.app.uniremington.repository.JornadaRepository;
import com.vetsync.app.uniremington.repository.ServicioSocialRepository;
import com.vetsync.app.uniremington.repository.UsuarioUniremingtonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocenteService {

    private final JornadaRepository jornadaRepository;
    private final ServicioSocialRepository servicioSocialRepository;
    private final UsuarioUniremingtonRepository usuarioRepository;

    public List<Jornada> getMisJornadas(String docenteId) {
        return jornadaRepository.findAll().stream()
                .filter(j -> j.getPersonalAsignado().stream().anyMatch(u -> u.getId().equals(docenteId)))
                .collect(Collectors.toList());
    }

    public List<UsuarioUniremington> getMisEstudiantes(String docenteId) {
        List<Jornada> jornadas = getMisJornadas(docenteId);
        Set<UsuarioUniremington> estudiantes = new HashSet<>();
        for (Jornada j : jornadas) {
            j.getPersonalAsignado().stream()
                    .filter(u -> "ESTUDIANTE".equalsIgnoreCase(u.getRol()))
                    .forEach(estudiantes::add);
        }
        return new ArrayList<>(estudiantes);
    }

    public List<ServicioSocial> getCasosPendientes(String docenteId) {
        List<Jornada> jornadas = getMisJornadas(docenteId);
        List<String> municipios = jornadas.stream().map(Jornada::getMunicipio).toList();
        
        return servicioSocialRepository.findAll().stream()
                .filter(s -> s.getBeneficiario() != null && municipios.contains(s.getBeneficiario().getMunicipio()))
                .filter(s -> !"FINALIZADO".equalsIgnoreCase(s.getEstado()) && !"CERRADO".equalsIgnoreCase(s.getEstado()))
                .collect(Collectors.toList());
    }

    public Map<String, Object> getMisEstadisticas(String docenteId) {
        List<Jornada> jornadas = getMisJornadas(docenteId);
        List<String> municipios = jornadas.stream().map(Jornada::getMunicipio).toList();
        
        long beneficiariosAtendidos = servicioSocialRepository.findAll().stream()
                .filter(s -> s.getBeneficiario() != null && municipios.contains(s.getBeneficiario().getMunicipio()))
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalJornadas", jornadas.size());
        stats.put("totalEstudiantes", getMisEstudiantes(docenteId).size());
        stats.put("beneficiariosAtendidos", beneficiariosAtendidos);
        stats.put("casosPendientes", getCasosPendientes(docenteId).size());
        
        return stats;
    }
}
