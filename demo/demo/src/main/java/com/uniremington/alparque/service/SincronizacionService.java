package com.uniremington.alparque.service;

import java.time.LocalDate;

import com.uniremington.alparque.dto.request.SincronizacionBatchRequestDTO;
import com.uniremington.alparque.dto.response.SincronizacionLotePageResponseDTO;
import com.uniremington.alparque.dto.response.SincronizacionResponseDTO;

public interface SincronizacionService {

	SincronizacionResponseDTO sincronizarBatch(SincronizacionBatchRequestDTO request);

	SincronizacionResponseDTO consultarLote(String loteId, String dispositivoId);

	SincronizacionLotePageResponseDTO listarLotesRecientes(String dispositivoId, String estadoLote, int page, int size);

	SincronizacionLotePageResponseDTO listarHistorialLotes(String dispositivoId,
			String estadoLote,
			LocalDate fechaInicio,
			LocalDate fechaFin,
			int page,
			int size);
}
