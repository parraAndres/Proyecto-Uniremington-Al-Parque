package com.uniremington.alparque.service.impl;

import com.uniremington.alparque.dto.request.BeneficiarioSyncItemDTO;
import com.uniremington.alparque.dto.request.SincronizacionBatchRequestDTO;
import com.uniremington.alparque.dto.response.SincronizacionItemResultadoDTO;
import com.uniremington.alparque.dto.response.SincronizacionLotePageResponseDTO;
import com.uniremington.alparque.dto.response.SincronizacionLoteResumenDTO;
import com.uniremington.alparque.dto.response.SincronizacionResponseDTO;
import com.uniremington.alparque.model.Beneficiario;
import com.uniremington.alparque.model.SincronizacionEvento;
import com.uniremington.alparque.model.SincronizacionLote;
import com.uniremington.alparque.model.SincronizacionResultadoItem;
import com.uniremington.alparque.model.enums.EstadoItem;
import com.uniremington.alparque.model.enums.EstadoLote;
import com.uniremington.alparque.repository.BeneficiarioRepository;
import com.uniremington.alparque.repository.ServicioRepository;
import com.uniremington.alparque.repository.SincronizacionEventoRepository;
import com.uniremington.alparque.repository.SincronizacionLoteRepository;
import com.uniremington.alparque.repository.SincronizacionResultadoItemRepository;
import com.uniremington.alparque.service.SincronizacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de {@link SincronizacionService}.
 *
 * <p>Algoritmo de procesamiento por ítem:
 * <ol>
 *   <li>Si el {@code idempotencyKey} ya existe → DUPLICATE (no reprocesa)</li>
 *   <li>Si el beneficiario ya existe en DB y el evento del cliente es más antiguo
 *       que el evento más reciente registrado → CONFLICT (se ignora el dato obsoleto)</li>
 *   <li>En caso contrario → UPSERT: CREATED si es nuevo, UPDATED si ya existía</li>
 * </ol>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SincronizacionServiceImpl implements SincronizacionService {

    private final BeneficiarioRepository beneficiarioRepository;
    private final ServicioRepository servicioRepository;
    private final SincronizacionEventoRepository sincronizacionEventoRepository;
    private final SincronizacionLoteRepository sincronizacionLoteRepository;
    private final SincronizacionResultadoItemRepository sincronizacionResultadoItemRepository;

    // ─────────────────────────────────────────────────────────────────────────
    //  PUBLIC API
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public SincronizacionResponseDTO sincronizarBatch(SincronizacionBatchRequestDTO request) {

        // 1. Recuperar o crear el lote
        SincronizacionLote lote = sincronizacionLoteRepository
                .findByLoteIdAndDispositivoId(request.getLoteId(), request.getDispositivoId())
                .orElseGet(() -> {
                    SincronizacionLote nuevo = new SincronizacionLote();
                    nuevo.setLoteId(request.getLoteId());
                    nuevo.setDispositivoId(request.getDispositivoId());
                    nuevo.setEstadoLote(EstadoLote.PROCESSING);
                    nuevo.setFechaLote(LocalDateTime.now());
                    return sincronizacionLoteRepository.save(nuevo);
                });

        // 2. Limpiar resultados previos si el lote ya existía (reenvío)
        sincronizacionResultadoItemRepository.deleteByLote(lote);

        // 3. Procesar beneficiarios
        List<SincronizacionItemResultadoDTO> resultados = new ArrayList<>();
        int procesados   = 0;
        int duplicados   = 0;
        int conflictos   = 0;
        int errores      = 0;

        List<BeneficiarioSyncItemDTO> beneficiarios =
                request.getBeneficiarios() != null ? request.getBeneficiarios() : List.of();

        for (BeneficiarioSyncItemDTO item : beneficiarios) {
            SincronizacionItemResultadoDTO resultado = procesarBeneficiario(lote, item);
            resultados.add(resultado);

            switch (resultado.getEstado()) {
                case "CREATED", "UPDATED" -> procesados++;
                case "DUPLICATE"          -> duplicados++;
                case "CONFLICT"           -> conflictos++;
                default                   -> errores++;
            }
        }

        // 4. Determinar estado final del lote
        int total = beneficiarios.size();
        EstadoLote estadoFinal;
        if (errores == 0 && conflictos == 0) {
            estadoFinal = EstadoLote.COMPLETED;
        } else if (procesados > 0 || duplicados > 0) {
            estadoFinal = EstadoLote.PARTIAL;
        } else {
            estadoFinal = EstadoLote.FAILED;
        }

        // 5. Actualizar lote
        lote.setEstadoLote(estadoFinal);
        lote.setTotalRecibidos(total);
        lote.setProcesados(procesados);
        lote.setDuplicados(duplicados);
        lote.setConflictos(conflictos);
        lote.setErrores(errores);
        sincronizacionLoteRepository.save(lote);

        // 6. Construir respuesta
        SincronizacionResponseDTO response = new SincronizacionResponseDTO();
        response.setLoteId(request.getLoteId());
        response.setDispositivoId(request.getDispositivoId());
        response.setEstadoLote(estadoFinal.name());
        response.setMensaje("Lote procesado");
        response.setTotalRecibidos(total);
        response.setProcesados(procesados);
        response.setDuplicados(duplicados);
        response.setConflictos(conflictos);
        response.setErrores(errores);
        response.setResultados(resultados);

        return response;
    }

    @Override
    public SincronizacionLotePageResponseDTO listarLotesRecientes(
            String dispositivoId, String estadoLote, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaLote").descending());
        Page<SincronizacionLote> pageResult;

        if (estadoLote != null && !estadoLote.isBlank()) {
            EstadoLote estado = EstadoLote.valueOf(estadoLote);
            pageResult = sincronizacionLoteRepository
                    .findByDispositivoIdAndEstadoLote(dispositivoId, estado, pageable);
        } else {
            pageResult = sincronizacionLoteRepository.findAll(pageable);
        }

        return toPageResponse(pageResult);
    }

    @Override
    public SincronizacionLotePageResponseDTO listarHistorialLotes(
            String dispositivoId, String estadoLote,
            LocalDate fechaInicio, LocalDate fechaFin,
            int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaLote").descending());
        EstadoLote estado = estadoLote != null ? EstadoLote.valueOf(estadoLote) : null;

        Page<SincronizacionLote> pageResult;
        if (estado != null && fechaInicio != null && fechaFin != null) {
            pageResult = sincronizacionLoteRepository
                    .findByDispositivoIdAndEstadoLoteAndFechaLoteBetween(
                            dispositivoId,
                            estado,
                            fechaInicio.atStartOfDay(),
                            fechaFin.plusDays(1).atStartOfDay(),
                            pageable);
        } else {
            pageResult = sincronizacionLoteRepository.findAll(pageable);
        }

        return toPageResponse(pageResult);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    private SincronizacionItemResultadoDTO procesarBeneficiario(
            SincronizacionLote lote, BeneficiarioSyncItemDTO item) {

        try {
            // Paso 1: Verificar idempotencia
            Optional<SincronizacionEvento> eventoExistente =
                    sincronizacionEventoRepository.findByIdempotencyKey(item.getIdempotencyKey());

            if (eventoExistente.isPresent()) {
                return guardarResultado(lote, item, EstadoItem.DUPLICATE,
                        eventoExistente.get().getEntidadId() != null
                                ? eventoExistente.get().getEntidadId().toString() : null,
                        "Evento ya procesado anteriormente");
            }

            // Paso 2: Buscar beneficiario existente por documento
            Optional<Beneficiario> beneficiarioOpt =
                    beneficiarioRepository.findByNumeroDocumento(item.getNumeroDocumento());

            if (beneficiarioOpt.isPresent()) {
                Beneficiario existente = beneficiarioOpt.get();

                // Paso 3: Verificar conflicto temporal
                Optional<SincronizacionEvento> ultimoEvento =
                        sincronizacionEventoRepository
                                .findTopByTipoEntidadAndEntidadIdOrderByFechaClienteDescFechaRegistroDesc(
                                        "BENEFICIARIO", existente.getId());

                boolean esObsoleto = ultimoEvento.isPresent()
                        && item.getClientUpdatedAt() != null
                        && ultimoEvento.get().getFechaCliente() != null
                        && !item.getClientUpdatedAt().isAfter(ultimoEvento.get().getFechaCliente());

                if (esObsoleto) {
                    return guardarResultado(lote, item, EstadoItem.CONFLICT,
                            existente.getId().toString(),
                            "Dato obsoleto: el servidor tiene una versión más reciente");
                }

                // Actualizar
                actualizarBeneficiario(existente, item);
                beneficiarioRepository.save(existente);
                registrarEvento(item, "BENEFICIARIO", existente.getId());

                return guardarResultado(lote, item, EstadoItem.UPDATED,
                        existente.getId().toString(), "Beneficiario actualizado");
            }

            // Crear nuevo beneficiario
            Beneficiario nuevo = Beneficiario.builder()
                    .nombre(item.getNombre())
                    .numeroDocumento(item.getNumeroDocumento())
                    .edad(item.getEdad())
                    .genero(item.getGenero())
                    .telefono(item.getTelefono())
                    .municipio(item.getMunicipio())
                    .barrioVereda(item.getBarrioVereda())
                    .tipoPoblacion(item.getTipoPoblacion())
                    .servicioSolicitado(item.getServicioSolicitado())
                    .autorizaDatos(item.getAutorizaDatos())
                    .build();

            nuevo = beneficiarioRepository.save(nuevo);
            registrarEvento(item, "BENEFICIARIO", nuevo.getId());

            return guardarResultado(lote, item, EstadoItem.CREATED,
                    nuevo.getId().toString(), "Beneficiario creado");

        } catch (Exception e) {
            log.error("[Sync] Error procesando beneficiario {}: {}", item.getIdempotencyKey(), e.getMessage(), e);
            return guardarResultado(lote, item, EstadoItem.ERROR, null, e.getMessage());
        }
    }

    private void actualizarBeneficiario(Beneficiario target, BeneficiarioSyncItemDTO src) {
        target.setNombre(src.getNombre());
        target.setEdad(src.getEdad());
        target.setGenero(src.getGenero());
        target.setTelefono(src.getTelefono());
        target.setMunicipio(src.getMunicipio());
        target.setBarrioVereda(src.getBarrioVereda());
        target.setTipoPoblacion(src.getTipoPoblacion());
        target.setServicioSolicitado(src.getServicioSolicitado());
        target.setAutorizaDatos(src.getAutorizaDatos());
    }

    private void registrarEvento(BeneficiarioSyncItemDTO item, String tipoEntidad, java.util.UUID entidadId) {
        SincronizacionEvento evento = new SincronizacionEvento();
        evento.setIdempotencyKey(item.getIdempotencyKey());
        evento.setTipoEntidad(tipoEntidad);
        evento.setEntidadId(entidadId);
        evento.setClientRecordId(item.getClientRecordId());
        evento.setFechaCliente(item.getClientUpdatedAt());
        sincronizacionEventoRepository.save(evento);
    }

    private SincronizacionItemResultadoDTO guardarResultado(
            SincronizacionLote lote, BeneficiarioSyncItemDTO item,
            EstadoItem estado, String serverId, String mensaje) {

        SincronizacionResultadoItem resultado = SincronizacionResultadoItem.builder()
                .lote(lote)
                .tipoEntidad("BENEFICIARIO")
                .idempotencyKey(item.getIdempotencyKey())
                .clientRecordId(item.getClientRecordId())
                .estado(estado)
                .serverId(serverId)
                .mensaje(mensaje)
                .build();
        sincronizacionResultadoItemRepository.save(resultado);

        return new SincronizacionItemResultadoDTO(
                "BENEFICIARIO",
                item.getIdempotencyKey(),
                item.getClientRecordId(),
                estado.name(),
                serverId,
                mensaje);
    }

    private SincronizacionLotePageResponseDTO toPageResponse(Page<SincronizacionLote> page) {
        List<SincronizacionLoteResumenDTO> content = page.getContent().stream()
                .map(l -> new SincronizacionLoteResumenDTO(
                        l.getLoteId(),
                        l.getDispositivoId(),
                        l.getEstadoLote() != null ? l.getEstadoLote().name() : null,
                        l.getTotalRecibidos() != null ? l.getTotalRecibidos() : 0,
                        l.getProcesados()    != null ? l.getProcesados()    : 0,
                        l.getDuplicados()    != null ? l.getDuplicados()    : 0,
                        l.getConflictos()    != null ? l.getConflictos()    : 0,
                        l.getErrores()       != null ? l.getErrores()       : 0,
                        l.getFechaLote()))
                .toList();

        return new SincronizacionLotePageResponseDTO(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
