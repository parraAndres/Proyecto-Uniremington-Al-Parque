package com.vetsync.app.uniremington.service;

import com.vetsync.app.uniremington.dto.sync.SyncDtos.*;
import com.vetsync.app.uniremington.entity.*;
import com.vetsync.app.uniremington.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de sincronización para el módulo "Uniremington al Parque".
 *
 * Implementa la estrategia UPSERT por UUID del frontend:
 *   - Si el registro NO existe  → crea (status: CREATED)
 *   - Si el registro YA existe  → actualiza campos mutables (status: UPDATED)
 *   - Si hay error              → registra y continúa (status: ERROR)
 *
 * Este comportamiento garantiza idempotencia: enviar el mismo lote dos veces
 * produce el mismo estado final sin duplicados.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UniSyncService {

    private final BeneficiarioUniRepository beneficiarioRepository;
    private final ServicioPrestadoRepository servicioRepository;
    private final SeguimientoRepository seguimientoRepository;
    private final DiagnosticoRepository diagnosticoRepository;
    private final AcademicoRepository academicoRepository;
    private final RecursoRepository recursoRepository;

    // ────────────────────────────────────────────────────────────────
    //  BENEFICIARIOS
    // ────────────────────────────────────────────────────────────────

    @Transactional
    public List<SyncItemResult> syncBeneficiarios(List<BeneficiarioDto> dtos) {
        List<SyncItemResult> results = new ArrayList<>();
        for (BeneficiarioDto dto : dtos) {
            try {
                boolean exists = beneficiarioRepository.existsById(dto.getId());
                Beneficiario entity = beneficiarioRepository.findById(dto.getId())
                        .orElse(new Beneficiario());

                entity.setId(dto.getId());
                entity.setNombre(dto.getNombre());
                entity.setDocumento(dto.getDocumento());
                entity.setEdad(dto.getEdad());
                entity.setGenero(dto.getGenero());
                entity.setTelefono(dto.getTelefono());
                entity.setMunicipio(dto.getMunicipio());
                entity.setBarrio(dto.getBarrio());
                entity.setTipoPoblacion(dto.getTipoPoblacion());
                entity.setServicioSolicitado(dto.getServicioSolicitado());
                entity.setAutorizaDatos(dto.getAutorizaDatos());
                entity.setFechaRegistro(dto.getFechaRegistro());

                beneficiarioRepository.save(entity);
                results.add(exists ? SyncItemResult.updated(dto.getId())
                                   : SyncItemResult.created(dto.getId()));
            } catch (Exception e) {
                log.error("[Sync] Error procesando beneficiario {}: {}", dto.getId(), e.getMessage());
                results.add(SyncItemResult.error(dto.getId(), e.getMessage()));
            }
        }
        return results;
    }

    // ────────────────────────────────────────────────────────────────
    //  SERVICIOS PRESTADOS
    // ────────────────────────────────────────────────────────────────

    @Transactional
    public List<SyncItemResult> syncServicios(List<ServicioDto> dtos) {
        List<SyncItemResult> results = new ArrayList<>();
        for (ServicioDto dto : dtos) {
            try {
                boolean exists = servicioRepository.existsById(dto.getId());
                ServicioPrestado entity = servicioRepository.findById(dto.getId())
                        .orElse(new ServicioPrestado());

                entity.setId(dto.getId());
                entity.setBeneficiarioId(dto.getBeneficiarioId());
                entity.setTipoServicio(dto.getTipoServicio());
                entity.setFacultadResponsable(dto.getFacultadResponsable());
                entity.setDescripcion(dto.getDescripcion());
                entity.setTiempoAtencion(dto.getTiempoAtencion());
                entity.setResultado(dto.getResultado());
                entity.setFechaAtencion(dto.getFechaAtencion());

                servicioRepository.save(entity);
                results.add(exists ? SyncItemResult.updated(dto.getId())
                                   : SyncItemResult.created(dto.getId()));
            } catch (Exception e) {
                log.error("[Sync] Error procesando servicio {}: {}", dto.getId(), e.getMessage());
                results.add(SyncItemResult.error(dto.getId(), e.getMessage()));
            }
        }
        return results;
    }

    // ────────────────────────────────────────────────────────────────
    //  SEGUIMIENTOS
    // ────────────────────────────────────────────────────────────────

    @Transactional
    public List<SyncItemResult> syncSeguimientos(List<SeguimientoDto> dtos) {
        List<SyncItemResult> results = new ArrayList<>();
        for (SeguimientoDto dto : dtos) {
            try {
                boolean exists = seguimientoRepository.existsById(dto.getId());
                Seguimiento entity = seguimientoRepository.findById(dto.getId())
                        .orElse(new Seguimiento());

                entity.setId(dto.getId());
                entity.setBeneficiarioId(dto.getBeneficiarioId());
                entity.setEstadoCaso(dto.getEstadoCaso());
                entity.setEvolucion(dto.getEvolucion());
                entity.setObservaciones(dto.getObservaciones());
                entity.setFechaSeguimiento(dto.getFechaSeguimiento());
                entity.setDatosExtra(dto.getDatosExtra());

                seguimientoRepository.save(entity);
                results.add(exists ? SyncItemResult.updated(dto.getId())
                                   : SyncItemResult.created(dto.getId()));
            } catch (Exception e) {
                log.error("[Sync] Error procesando seguimiento {}: {}", dto.getId(), e.getMessage());
                results.add(SyncItemResult.error(dto.getId(), e.getMessage()));
            }
        }
        return results;
    }

    // ────────────────────────────────────────────────────────────────
    //  DIAGNÓSTICOS
    // ────────────────────────────────────────────────────────────────

    @Transactional
    public List<SyncItemResult> syncDiagnosticos(List<DiagnosticoDto> dtos) {
        List<SyncItemResult> results = new ArrayList<>();
        for (DiagnosticoDto dto : dtos) {
            try {
                boolean exists = diagnosticoRepository.existsById(dto.getId());
                Diagnostico entity = diagnosticoRepository.findById(dto.getId())
                        .orElse(new Diagnostico());

                entity.setId(dto.getId());
                entity.setBeneficiarioId(dto.getBeneficiarioId());
                entity.setTipo(dto.getTipo());
                entity.setDescripcion(dto.getDescripcion());
                entity.setDatos(dto.getDatos());
                entity.setFechaDiagnostico(dto.getFechaDiagnostico());

                diagnosticoRepository.save(entity);
                results.add(exists ? SyncItemResult.updated(dto.getId())
                                   : SyncItemResult.created(dto.getId()));
            } catch (Exception e) {
                log.error("[Sync] Error procesando diagnóstico {}: {}", dto.getId(), e.getMessage());
                results.add(SyncItemResult.error(dto.getId(), e.getMessage()));
            }
        }
        return results;
    }

    // ────────────────────────────────────────────────────────────────
    //  ACADÉMICO
    // ────────────────────────────────────────────────────────────────

    @Transactional
    public List<SyncItemResult> syncAcademico(List<AcademicoDto> dtos) {
        List<SyncItemResult> results = new ArrayList<>();
        for (AcademicoDto dto : dtos) {
            try {
                boolean exists = academicoRepository.existsById(dto.getId());
                Academico entity = academicoRepository.findById(dto.getId())
                        .orElse(new Academico());

                entity.setId(dto.getId());
                entity.setEstudianteId(dto.getEstudianteId());
                entity.setNombreEstudiante(dto.getNombreEstudiante());
                entity.setPrograma(dto.getPrograma());
                entity.setFacultad(dto.getFacultad());
                entity.setHorasReportadas(dto.getHorasReportadas());
                entity.setFechaActividad(dto.getFechaActividad());
                entity.setTipoParticipacion(dto.getTipoParticipacion());
                entity.setDatosExtra(dto.getDatosExtra());

                academicoRepository.save(entity);
                results.add(exists ? SyncItemResult.updated(dto.getId())
                                   : SyncItemResult.created(dto.getId()));
            } catch (Exception e) {
                log.error("[Sync] Error procesando académico {}: {}", dto.getId(), e.getMessage());
                results.add(SyncItemResult.error(dto.getId(), e.getMessage()));
            }
        }
        return results;
    }

    // ────────────────────────────────────────────────────────────────
    //  RECURSOS / APORTES
    // ────────────────────────────────────────────────────────────────

    @Transactional
    public List<SyncItemResult> syncRecursos(List<RecursoDto> dtos) {
        List<SyncItemResult> results = new ArrayList<>();
        for (RecursoDto dto : dtos) {
            try {
                boolean exists = recursoRepository.existsById(dto.getId());
                Recurso entity = recursoRepository.findById(dto.getId())
                        .orElse(new Recurso());

                entity.setId(dto.getId());
                entity.setTipoAporte(dto.getTipoAporte());
                entity.setFuente(dto.getFuente());
                entity.setAportante(dto.getAportante());
                entity.setDescripcion(dto.getDescripcion());
                entity.setValorMonetario(dto.getValorMonetario());
                entity.setCantidad(dto.getCantidad());
                entity.setUnidadMedida(dto.getUnidadMedida());
                entity.setFechaRegistro(dto.getFechaRegistro());
                entity.setFacultadAsociada(dto.getFacultadAsociada());
                entity.setDatosExtra(dto.getDatosExtra());

                recursoRepository.save(entity);
                results.add(exists ? SyncItemResult.updated(dto.getId())
                                   : SyncItemResult.created(dto.getId()));
            } catch (Exception e) {
                log.error("[Sync] Error procesando recurso {}: {}", dto.getId(), e.getMessage());
                results.add(SyncItemResult.error(dto.getId(), e.getMessage()));
            }
        }
        return results;
    }
}
