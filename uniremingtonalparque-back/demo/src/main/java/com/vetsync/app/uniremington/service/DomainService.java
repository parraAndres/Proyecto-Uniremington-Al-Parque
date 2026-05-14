package com.vetsync.app.uniremington.service;

import com.vetsync.app.exception.ReglaDeNegocioException;
import com.vetsync.app.uniremington.dto.DomainRequests;
import com.vetsync.app.uniremington.entity.*;
import com.vetsync.app.uniremington.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DomainService {

    private final BeneficiarioRepository beneficiarioRepository;
    private final ServicioSocialRepository servicioSocialRepository;
    private final SeguimientoCasoRepository seguimientoCasoRepository;
    private final ParticipacionAcademicaRepository participacionAcademicaRepository;
    private final RecursoAporteRepository recursoAporteRepository;

    @Transactional
    public Beneficiario upsertBeneficiario(DomainRequests.BeneficiarioRequest request) {
        Beneficiario beneficiario = beneficiarioRepository.findByDocumento(request.getDocumento())
                .orElseGet(Beneficiario::new);
        // Campos actualizados según nueva entidad Beneficiario (UUID-based)
        if (beneficiario.getId() == null) {
            beneficiario.setId(java.util.UUID.randomUUID().toString());
        }
        beneficiario.setDocumento(request.getDocumento());
        // Compatibilidad: combinar nombres + apellidos en el campo nombre único
        String nombreCompleto = (request.getNombres() != null ? request.getNombres() : "")
                + " " + (request.getApellidos() != null ? request.getApellidos() : "");
        beneficiario.setNombre(nombreCompleto.trim());
        beneficiario.setTelefono(request.getTelefono());
        beneficiario.setMunicipio(request.getMunicipio());
        beneficiario.setBarrio(request.getBarrio());
        beneficiario.setAutorizaDatos(request.getConsentimientoDatos());
        return beneficiarioRepository.save(beneficiario);
    }

    @Transactional
    public ServicioSocial createServicio(DomainRequests.ServicioRequest request) {
        Beneficiario beneficiario = beneficiarioRepository.findByDocumento(request.getBeneficiarioDocumento())
                .orElseThrow(() -> new ReglaDeNegocioException("Beneficiario no encontrado por documento"));

        ServicioSocial servicio = ServicioSocial.builder()
                .beneficiario(beneficiario)
                .facultad(request.getFacultad())
                .tipoServicio(request.getTipoServicio())
                .resultadoAtencion(request.getResultadoAtencion())
                .fechaServicio(request.getFechaServicio())
                .observaciones(request.getObservaciones())
                .estudianteId(request.getEstudianteId())
                .duracionMinutos(request.getDuracionMinutos())
                .build();
        return servicioSocialRepository.save(servicio);
    }

    @Transactional
    public SeguimientoCaso createSeguimiento(DomainRequests.SeguimientoRequest request) {
        Beneficiario beneficiario = beneficiarioRepository.findByDocumento(request.getBeneficiarioDocumento())
                .orElseThrow(() -> new ReglaDeNegocioException("Beneficiario no encontrado por documento"));
        SeguimientoCaso seguimiento = SeguimientoCaso.builder()
                .beneficiario(beneficiario)
                .casoId(request.getCasoId())
                .estadoCaso(request.getEstadoCaso())
                .evolucion(request.getEvolucion())
                .fechaEstado(request.getFechaEstado())
                .build();
        return seguimientoCasoRepository.save(seguimiento);
    }

    @Transactional
    public ParticipacionAcademica createAcademico(DomainRequests.AcademicoRequest request) {
        ParticipacionAcademica participacion = ParticipacionAcademica.builder()
                .estudianteId(request.getEstudianteId())
                .nombreEstudiante(request.getNombreEstudiante())
                .programa(request.getPrograma())
                .facultad(request.getFacultad())
                .horasReportadas(request.getHorasReportadas())
                .fechaActividad(request.getFechaActividad())
                .tipoParticipacion(request.getTipoParticipacion())
                .build();
        return participacionAcademicaRepository.save(participacion);
    }

    @Transactional
    public RecursoAporte createRecurso(DomainRequests.RecursoRequest request) {
        RecursoAporte recurso = RecursoAporte.builder()
                .tipoAporte(request.getTipoAporte())
                .fuente(request.getFuente())
                .aportante(request.getAportante())
                .descripcion(request.getDescripcion())
                .valorMonetario(request.getValorMonetario())
                .cantidad(request.getCantidad())
                .unidadMedida(request.getUnidadMedida())
                .fechaRegistro(request.getFechaRegistro())
                .facultadAsociada(request.getFacultadAsociada())
                .build();
        return recursoAporteRepository.save(recurso);
    }
    @Transactional(readOnly = true)
    public java.util.List<ServicioSocial> getServiciosByEstudiante(String estudianteId) {
        return servicioSocialRepository.findByEstudianteIdOrderByFechaServicioDesc(estudianteId);
    }
}
