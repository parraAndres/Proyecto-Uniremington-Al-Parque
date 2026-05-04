package com.vetsync.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetsync.app.dto.request.CitaRequest;
import com.vetsync.app.dto.request.ClienteRequest;
import com.vetsync.app.dto.request.MascotaRequest;
import com.vetsync.app.dto.request.SincronizacionBatchRequest;
import com.vetsync.app.dto.response.SincronizacionResponse;
import com.vetsync.app.entity.LoteSincronizacion;
import com.vetsync.app.repository.LoteSincronizacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SincronizacionService {

    private final LoteSincronizacionRepository loteRepository;
    private final ClienteService clienteService;
    private final MascotaService mascotaService;
    private final CitaService citaService;
    private final ObjectMapper objectMapper;

    @Transactional
    public SincronizacionResponse procesarLote(SincronizacionBatchRequest request) {
        String clave = request.getClaveIdempotencia();

        // --- Deduplicación: Last-Write-Wins ---
        Optional<LoteSincronizacion> existente = loteRepository.findByClaveIdempotencia(clave);
        if (existente.isPresent()) {
            LoteSincronizacion lote = existente.get();
            if (request.getLastModified().isBefore(lote.getLastModified()) ||
                request.getLastModified().isEqual(lote.getLastModified())) {
                log.info("Lote duplicado detectado (LWW): clave={}", clave);
                return buildResponse(lote, "Lote duplicado ignorado (Last-Write-Wins)");
            }
            log.info("Lote más reciente detectado, re-procesando: clave={}", clave);
        }

        int procesados = 0, duplicados = 0, errores = 0;
        LoteSincronizacion.EstadoLote estadoFinal;

        for (SincronizacionBatchRequest.RegistroOfflineDto reg : request.getRegistros()) {
            try {
                procesarRegistro(reg);
                procesados++;
            } catch (Exception e) {
                log.error("Error procesando registro tipo={}: {}", reg.getTipo(), e.getMessage());
                errores++;
            }
        }

        if (errores == 0) estadoFinal = LoteSincronizacion.EstadoLote.EXITOSO;
        else if (procesados > 0) estadoFinal = LoteSincronizacion.EstadoLote.PARCIAL;
        else estadoFinal = LoteSincronizacion.EstadoLote.ERROR;

        LoteSincronizacion lote = LoteSincronizacion.builder()
                .claveIdempotencia(clave)
                .origen(request.getOrigen())
                .totalRegistros(request.getRegistros().size())
                .procesados(procesados)
                .duplicados(duplicados)
                .errores(errores)
                .estado(estadoFinal)
                .lastModified(request.getLastModified())
                .fechaProcesado(LocalDateTime.now())
                .build();

        loteRepository.save(lote);
        return buildResponse(lote, "Lote procesado correctamente");
    }

    private void procesarRegistro(SincronizacionBatchRequest.RegistroOfflineDto reg) {
        log.debug("Procesando registro tipo={} operacion={}", reg.getTipo(), reg.getOperacion());
        switch (reg.getTipo().toUpperCase()) {
            case "CLIENTE" -> {
                ClienteRequest req = objectMapper.convertValue(reg.getPayload(), ClienteRequest.class);
                if ("CREATE".equalsIgnoreCase(reg.getOperacion())) clienteService.create(req);
                else if ("UPDATE".equalsIgnoreCase(reg.getOperacion()) && reg.getEntidadId() != null)
                    clienteService.update(reg.getEntidadId(), req);
            }
            case "MASCOTA" -> {
                MascotaRequest req = objectMapper.convertValue(reg.getPayload(), MascotaRequest.class);
                if ("CREATE".equalsIgnoreCase(reg.getOperacion())) mascotaService.create(req);
                else if ("UPDATE".equalsIgnoreCase(reg.getOperacion()) && reg.getEntidadId() != null)
                    mascotaService.update(reg.getEntidadId(), req);
            }
            case "CITA" -> {
                CitaRequest req = objectMapper.convertValue(reg.getPayload(), CitaRequest.class);
                if ("CREATE".equalsIgnoreCase(reg.getOperacion())) citaService.create(req);
                else if ("UPDATE".equalsIgnoreCase(reg.getOperacion()) && reg.getEntidadId() != null)
                    citaService.update(reg.getEntidadId(), req);
            }
            default -> throw new IllegalArgumentException("Tipo de entidad no soportado: " + reg.getTipo());
        }
    }

    private SincronizacionResponse buildResponse(LoteSincronizacion lote, String mensaje) {
        return SincronizacionResponse.builder()
                .claveIdempotencia(lote.getClaveIdempotencia())
                .estado(lote.getEstado())
                .totalRegistros(lote.getTotalRegistros())
                .procesados(lote.getProcesados())
                .duplicados(lote.getDuplicados())
                .errores(lote.getErrores())
                .fechaProcesado(lote.getFechaProcesado())
                .mensaje(mensaje)
                .build();
    }
}
