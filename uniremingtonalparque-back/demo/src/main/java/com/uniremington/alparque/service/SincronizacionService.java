package com.uniremington.alparque.service;

import com.uniremington.alparque.dto.request.SincronizacionBatchRequestDTO;
import com.uniremington.alparque.dto.response.SincronizacionLotePageResponseDTO;
import com.uniremington.alparque.dto.response.SincronizacionResponseDTO;

import java.time.LocalDate;

/**
 * Contrato del servicio de sincronización por lotes para el módulo "Uniremington al Parque".
 *
 * <p>Estrategia de idempotencia:
 * <ul>
 *   <li>Si ya existe el {@code idempotencyKey}   → devuelve DUPLICATE (no reprocesa).</li>
 *   <li>Si el beneficiario ya existe pero el evento del cliente es más antiguo → CONFLICT.</li>
 *   <li>En caso contrario                         → UPSERT (CREATED / UPDATED).</li>
 * </ul>
 */
public interface SincronizacionService {

    /**
     * Procesa un lote completo de sincronización y devuelve un resumen con el resultado
     * de cada ítem.
     *
     * @param request Lote de sincronización enviado por el dispositivo
     * @return Resumen con conteos y resultados individuales
     */
    SincronizacionResponseDTO sincronizarBatch(SincronizacionBatchRequestDTO request);

    /**
     * Lista los lotes recientes de un dispositivo, filtrados por estado, de forma paginada.
     *
     * @param dispositivoId ID del dispositivo
     * @param estadoLote    Estado del lote (COMPLETED, PARTIAL, etc.) — puede ser null para todos
     * @param page          Número de página (0-indexed)
     * @param size          Tamaño de página
     * @return Página de resúmenes de lotes
     */
    SincronizacionLotePageResponseDTO listarLotesRecientes(String dispositivoId, String estadoLote, int page, int size);

    /**
     * Lista el historial de lotes de un dispositivo en un rango de fechas.
     *
     * @param dispositivoId ID del dispositivo
     * @param estadoLote    Estado del lote — puede ser null para todos
     * @param fechaInicio   Fecha inicial del rango (inclusive)
     * @param fechaFin      Fecha final del rango (inclusive)
     * @param page          Número de página (0-indexed)
     * @param size          Tamaño de página
     * @return Página de resúmenes de lotes
     */
    SincronizacionLotePageResponseDTO listarHistorialLotes(
            String dispositivoId,
            String estadoLote,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            int page,
            int size);
}
