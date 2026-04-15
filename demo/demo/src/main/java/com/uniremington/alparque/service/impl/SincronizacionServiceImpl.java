package com.uniremington.alparque.service.impl;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uniremington.alparque.dto.request.BeneficiarioSyncItemDTO;
import com.uniremington.alparque.dto.request.ServicioSyncItemDTO;
import com.uniremington.alparque.dto.request.SincronizacionBatchRequestDTO;
import com.uniremington.alparque.dto.response.SincronizacionItemResultadoDTO;
import com.uniremington.alparque.dto.response.SincronizacionLotePageResponseDTO;
import com.uniremington.alparque.dto.response.SincronizacionLoteResumenDTO;
import com.uniremington.alparque.dto.response.SincronizacionResponseDTO;
import com.uniremington.alparque.model.Beneficiario;
import com.uniremington.alparque.model.Servicio;
import com.uniremington.alparque.model.SincronizacionEvento;
import com.uniremington.alparque.model.SincronizacionLote;
import com.uniremington.alparque.model.SincronizacionResultadoItem;
import com.uniremington.alparque.exception.ResourceNotFoundException;
import com.uniremington.alparque.repository.BeneficiarioRepository;
import com.uniremington.alparque.repository.ServicioRepository;
import com.uniremington.alparque.repository.SincronizacionEventoRepository;
import com.uniremington.alparque.repository.SincronizacionLoteRepository;
import com.uniremington.alparque.repository.SincronizacionResultadoItemRepository;
import com.uniremington.alparque.service.SincronizacionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SincronizacionServiceImpl implements SincronizacionService {

	private static final String BENEFICIARIO = "BENEFICIARIO";
	private static final String SERVICIO = "SERVICIO";
	private static final String CREATED = "CREATED";
	private static final String UPDATED = "UPDATED";
	private static final String DUPLICATE = "DUPLICATE";
	private static final String STALE_IGNORED = "STALE_IGNORED";
	private static final String ERROR = "ERROR";
	private static final String LOTE_PENDING = "PENDING";
	private static final String LOTE_PARTIAL = "PARTIAL";
	private static final String LOTE_COMPLETED = "COMPLETED";

	private final BeneficiarioRepository beneficiarioRepository;
	private final ServicioRepository servicioRepository;
	private final SincronizacionEventoRepository sincronizacionEventoRepository;
	private final SincronizacionLoteRepository sincronizacionLoteRepository;
	private final SincronizacionResultadoItemRepository sincronizacionResultadoItemRepository;

	private record SyncMetrics(int procesados, int duplicados, int conflictos, int errores) {

		SyncMetrics add(SyncMetrics other) {
			return new SyncMetrics(
				procesados + other.procesados,
				duplicados + other.duplicados,
				conflictos + other.conflictos,
				errores + other.errores
			);
		}
	}

	private record EventoMetadata(String loteId,
			String dispositivoId,
			String clientRecordId,
			java.time.LocalDateTime fechaCliente) {
	}

	@Override
	public SincronizacionResponseDTO sincronizarBatch(SincronizacionBatchRequestDTO request) {
		SincronizacionLote lote = sincronizacionLoteRepository
			.findByLoteIdAndDispositivoId(request.getLoteId(), request.getDispositivoId())
			.orElseGet(() -> crearLote(request.getLoteId(), request.getDispositivoId()));

		if (LOTE_COMPLETED.equals(lote.getEstadoLote())) {
			return buildResponseLoteYaProcesado(lote);
		}

		lote.setEstadoLote(LOTE_PENDING);
		sincronizacionLoteRepository.save(lote);

		List<SincronizacionItemResultadoDTO> resultados = new ArrayList<>();

		List<BeneficiarioSyncItemDTO> beneficiarios = request.getBeneficiarios() == null
			? List.of()
			: request.getBeneficiarios();

		List<ServicioSyncItemDTO> servicios = request.getServicios() == null
			? List.of()
			: request.getServicios();

		SyncMetrics metricasBeneficiario = procesarBeneficiarios(beneficiarios, request, resultados);
		SyncMetrics metricasServicio = procesarServicios(servicios, request, resultados);
		SyncMetrics metricas = metricasBeneficiario.add(metricasServicio);

		int totalRecibidos = beneficiarios.size() + servicios.size();
		String estadoLote = metricas.errores > 0 || metricas.conflictos > 0 ? LOTE_PARTIAL : LOTE_COMPLETED;

		lote.setTotalRecibidos(totalRecibidos);
		lote.setProcesados(metricas.procesados);
		lote.setDuplicados(metricas.duplicados);
		lote.setConflictos(metricas.conflictos);
		lote.setErrores(metricas.errores);
		lote.setEstadoLote(estadoLote);
		sincronizacionLoteRepository.save(lote);
		persistirResultadosLote(lote, resultados);

		return buildResponse(lote, resultados, "Lote procesado");
	}

	@Override
	@Transactional(readOnly = true)
	public SincronizacionResponseDTO consultarLote(String loteId, String dispositivoId) {
		SincronizacionLote lote = sincronizacionLoteRepository
			.findByLoteIdAndDispositivoId(loteId, dispositivoId)
			.orElseThrow(() -> new ResourceNotFoundException("No existe lote para loteId=" + loteId + " y dispositivoId=" + dispositivoId));

		List<SincronizacionItemResultadoDTO> resultados = mapResultados(
			sincronizacionResultadoItemRepository.findByLoteOrderByFechaRegistroAsc(lote));
		return buildResponse(lote, resultados, "Consulta de lote");
	}

	@Override
	@Transactional(readOnly = true)
	public SincronizacionLotePageResponseDTO listarLotesRecientes(String dispositivoId, String estadoLote, int page, int size) {
		if (dispositivoId == null || dispositivoId.isBlank()) {
			throw new IllegalArgumentException("dispositivoId es obligatorio");
		}

		int safePage = Math.max(page, 0);
		int safeSize = Math.min(Math.max(size, 1), 100);
		Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "fechaActualizacion"));

		Page<SincronizacionLote> lotes = (estadoLote == null || estadoLote.isBlank())
			? sincronizacionLoteRepository.findByDispositivoId(dispositivoId, pageable)
			: sincronizacionLoteRepository.findByDispositivoIdAndEstadoLote(dispositivoId, estadoLote, pageable);

		List<SincronizacionLoteResumenDTO> content = lotes
			.getContent()
			.stream()
			.map(this::toResumen)
			.toList();

		return new SincronizacionLotePageResponseDTO(
			content,
			lotes.getNumber(),
			lotes.getSize(),
			lotes.getTotalPages(),
			lotes.getTotalElements()
		);
	}

	@Override
	@Transactional(readOnly = true)
	public SincronizacionLotePageResponseDTO listarHistorialLotes(String dispositivoId,
			String estadoLote,
			LocalDate fechaInicio,
			LocalDate fechaFin,
			int page,
			int size) {
		if (dispositivoId == null || dispositivoId.isBlank()) {
			throw new IllegalArgumentException("dispositivoId es obligatorio");
		}
		if (fechaInicio == null || fechaFin == null) {
			throw new IllegalArgumentException("fechaInicio y fechaFin son obligatorias");
		}
		if (fechaInicio.isAfter(fechaFin)) {
			throw new IllegalArgumentException("fechaInicio no puede ser mayor que fechaFin");
		}

		LocalDateTime inicio = fechaInicio.atStartOfDay();
		LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);
		int safePage = Math.max(page, 0);
		int safeSize = Math.min(Math.max(size, 1), 100);
		Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "fechaActualizacion"));

		Page<SincronizacionLote> lotes = (estadoLote == null || estadoLote.isBlank())
			? sincronizacionLoteRepository.findByDispositivoIdAndFechaActualizacionBetween(dispositivoId, inicio, fin, pageable)
			: sincronizacionLoteRepository.findByDispositivoIdAndEstadoLoteAndFechaActualizacionBetween(dispositivoId,
					estadoLote,
					inicio,
					fin,
					pageable);

		List<SincronizacionLoteResumenDTO> content = lotes.getContent().stream().map(this::toResumen).toList();
		return new SincronizacionLotePageResponseDTO(
			content,
			lotes.getNumber(),
			lotes.getSize(),
			lotes.getTotalPages(),
			lotes.getTotalElements());
	}

	private SyncMetrics procesarBeneficiarios(List<BeneficiarioSyncItemDTO> items,
			SincronizacionBatchRequestDTO request,
			List<SincronizacionItemResultadoDTO> resultados) {
		int procesados = 0;
		int duplicados = 0;
		int conflictos = 0;
		int errores = 0;

		for (BeneficiarioSyncItemDTO item : items) {
			SincronizacionItemResultadoDTO resultado = procesarBeneficiario(item,
					new EventoMetadata(request.getLoteId(), request.getDispositivoId(), item.getClientRecordId(), item.getClientUpdatedAt()));
			resultados.add(resultado);
			if (DUPLICATE.equals(resultado.getEstado())) {
				duplicados++;
			} else if (STALE_IGNORED.equals(resultado.getEstado())) {
				conflictos++;
			} else if (ERROR.equals(resultado.getEstado())) {
				errores++;
			} else {
				procesados++;
			}
		}

		return new SyncMetrics(procesados, duplicados, conflictos, errores);
	}

	private SyncMetrics procesarServicios(List<ServicioSyncItemDTO> items,
			SincronizacionBatchRequestDTO request,
			List<SincronizacionItemResultadoDTO> resultados) {
		int procesados = 0;
		int duplicados = 0;
		int conflictos = 0;
		int errores = 0;

		for (ServicioSyncItemDTO item : items) {
			SincronizacionItemResultadoDTO resultado = procesarServicio(item,
					new EventoMetadata(request.getLoteId(), request.getDispositivoId(), item.getClientRecordId(), item.getClientUpdatedAt()));
			resultados.add(resultado);
			if (DUPLICATE.equals(resultado.getEstado())) {
				duplicados++;
			} else if (STALE_IGNORED.equals(resultado.getEstado())) {
				conflictos++;
			} else if (ERROR.equals(resultado.getEstado())) {
				errores++;
			} else {
				procesados++;
			}
		}

		return new SyncMetrics(procesados, duplicados, conflictos, errores);
	}

	private SincronizacionItemResultadoDTO procesarBeneficiario(BeneficiarioSyncItemDTO item,
			EventoMetadata metadata) {
		Optional<SincronizacionEvento> eventoExistente = sincronizacionEventoRepository.findByIdempotencyKey(item.getIdempotencyKey());
		if (eventoExistente.isPresent()) {
			return new SincronizacionItemResultadoDTO(
				BENEFICIARIO,
				item.getIdempotencyKey(),
				item.getClientRecordId(),
				DUPLICATE,
				eventoExistente.get().getEntidadId().toString(),
				"Item previamente sincronizado"
			);
		}

		try {
			Beneficiario beneficiario;
			String estado;
			Optional<Beneficiario> existente = beneficiarioRepository.findByNumeroDocumento(item.getNumeroDocumento());

			if (existente.isPresent()) {
				beneficiario = existente.get();

				if (esEventoStale(beneficiario.getId(), item.getClientUpdatedAt())) {
					return new SincronizacionItemResultadoDTO(
						BENEFICIARIO,
						item.getIdempotencyKey(),
						item.getClientRecordId(),
						STALE_IGNORED,
						beneficiario.getId().toString(),
						"Evento desfasado ignorado por regla last-write-wins"
					);
				}

				estado = UPDATED;
			} else {
				beneficiario = new Beneficiario();
				estado = CREATED;
			}

			mapBeneficiario(item, beneficiario);
			Beneficiario saved = beneficiarioRepository.save(beneficiario);
			registrarEvento(item.getIdempotencyKey(), metadata, BENEFICIARIO, saved.getId(), estado);

			return new SincronizacionItemResultadoDTO(
				BENEFICIARIO,
				item.getIdempotencyKey(),
				item.getClientRecordId(),
				estado,
				saved.getId().toString(),
				CREATED.equals(estado) ? "Beneficiario creado" : "Beneficiario deduplicado y actualizado"
			);
		} catch (Exception ex) {
			return new SincronizacionItemResultadoDTO(
				BENEFICIARIO,
				item.getIdempotencyKey(),
				item.getClientRecordId(),
				ERROR,
				null,
				ex.getMessage()
			);
		}
	}

	private SincronizacionItemResultadoDTO procesarServicio(ServicioSyncItemDTO item,
			EventoMetadata metadata) {
		Optional<SincronizacionEvento> eventoExistente = sincronizacionEventoRepository.findByIdempotencyKey(item.getIdempotencyKey());
		if (eventoExistente.isPresent()) {
			return new SincronizacionItemResultadoDTO(
				SERVICIO,
				item.getIdempotencyKey(),
				item.getClientRecordId(),
				DUPLICATE,
				eventoExistente.get().getEntidadId().toString(),
				"Item previamente sincronizado"
			);
		}

		try {
			Beneficiario beneficiario = beneficiarioRepository.findByNumeroDocumento(item.getBeneficiarioNumeroDocumento())
				.orElseThrow(() -> new IllegalArgumentException("No existe beneficiario para documento: " + item.getBeneficiarioNumeroDocumento()));

			Servicio servicio = new Servicio();
			servicio.setBeneficiario(beneficiario);
			servicio.setTipoServicio(item.getTipoServicio());
			servicio.setFacultad(item.getFacultad());
			servicio.setDescripcionAtencion(item.getDescripcionAtencion());
			servicio.setTiempoAtencionMinutos(item.getTiempoAtencionMinutos());
			servicio.setResultado(item.getResultado());
			servicio.setObservaciones(item.getObservaciones());
			servicio.setEvidencias(item.getEvidencias());

			Servicio saved = servicioRepository.save(servicio);
			registrarEvento(item.getIdempotencyKey(), metadata, SERVICIO, saved.getId(), CREATED);

			return new SincronizacionItemResultadoDTO(
				SERVICIO,
				item.getIdempotencyKey(),
				item.getClientRecordId(),
				CREATED,
				saved.getId().toString(),
				"Servicio creado"
			);
		} catch (Exception ex) {
			return new SincronizacionItemResultadoDTO(
				SERVICIO,
				item.getIdempotencyKey(),
				item.getClientRecordId(),
				ERROR,
				null,
				ex.getMessage()
			);
		}
	}

	private void mapBeneficiario(BeneficiarioSyncItemDTO item, Beneficiario beneficiario) {
		beneficiario.setNombre(item.getNombre());
		beneficiario.setNumeroDocumento(item.getNumeroDocumento());
		beneficiario.setEdad(item.getEdad());
		beneficiario.setGenero(item.getGenero());
		beneficiario.setTelefono(item.getTelefono());
		beneficiario.setMunicipio(item.getMunicipio());
		beneficiario.setBarrioVereda(item.getBarrioVereda());
		beneficiario.setTipoPoblacion(item.getTipoPoblacion());
		beneficiario.setServicioSolicitado(item.getServicioSolicitado());
		beneficiario.setAutorizaDatos(item.getAutorizaDatos());
	}

	private boolean esEventoStale(UUID beneficiarioId, java.time.LocalDateTime fechaClienteEvento) {
		if (fechaClienteEvento == null) {
			return false;
		}

		Optional<SincronizacionEvento> eventoMasReciente = sincronizacionEventoRepository
			.findTopByTipoEntidadAndEntidadIdOrderByFechaClienteDescFechaRegistroDesc(BENEFICIARIO, beneficiarioId);

		if (eventoMasReciente.isEmpty() || eventoMasReciente.get().getFechaCliente() == null) {
			return false;
		}

		return fechaClienteEvento.isBefore(eventoMasReciente.get().getFechaCliente());
	}

	private void registrarEvento(String idempotencyKey,
			EventoMetadata metadata,
			String tipoEntidad,
			UUID entidadId,
			String estado) {
		SincronizacionEvento evento = new SincronizacionEvento();
		evento.setIdempotencyKey(idempotencyKey);
		evento.setLoteId(metadata.loteId);
		evento.setDispositivoId(metadata.dispositivoId);
		evento.setClientRecordId(metadata.clientRecordId);
		evento.setFechaCliente(metadata.fechaCliente);
		evento.setTipoEntidad(tipoEntidad);
		evento.setEntidadId(entidadId);
		evento.setEstado(estado);
		sincronizacionEventoRepository.save(evento);
	}

	private SincronizacionLote crearLote(String loteId, String dispositivoId) {
		SincronizacionLote lote = new SincronizacionLote();
		lote.setLoteId(loteId);
		lote.setDispositivoId(dispositivoId);
		lote.setEstadoLote(LOTE_PENDING);
		return sincronizacionLoteRepository.save(lote);
	}

	private SincronizacionResponseDTO buildResponseLoteYaProcesado(SincronizacionLote lote) {
		List<SincronizacionItemResultadoDTO> resultados = mapResultados(
			sincronizacionResultadoItemRepository.findByLoteOrderByFechaRegistroAsc(lote));
		return buildResponse(lote, resultados, "Lote ya procesado anteriormente");
	}

	private SincronizacionResponseDTO buildResponse(SincronizacionLote lote,
			List<SincronizacionItemResultadoDTO> resultados,
			String mensaje) {
		SincronizacionResponseDTO response = new SincronizacionResponseDTO();
		response.setLoteId(lote.getLoteId());
		response.setDispositivoId(lote.getDispositivoId());
		response.setEstadoLote(lote.getEstadoLote());
		response.setMensaje(mensaje);
		response.setTotalRecibidos(lote.getTotalRecibidos());
		response.setProcesados(lote.getProcesados());
		response.setDuplicados(lote.getDuplicados());
		response.setConflictos(lote.getConflictos());
		response.setErrores(lote.getErrores());
		response.setResultados(resultados);
		return response;
	}

	private void persistirResultadosLote(SincronizacionLote lote, List<SincronizacionItemResultadoDTO> resultados) {
		sincronizacionResultadoItemRepository.deleteByLote(lote);
		for (SincronizacionItemResultadoDTO resultado : resultados) {
			SincronizacionResultadoItem entity = new SincronizacionResultadoItem();
			entity.setLote(lote);
			entity.setTipoEntidad(resultado.getTipoEntidad());
			entity.setIdempotencyKey(resultado.getIdempotencyKey());
			entity.setClientRecordId(resultado.getClientRecordId());
			entity.setEstado(resultado.getEstado());
			entity.setServerId(resultado.getServerId());
			entity.setMensaje(resultado.getMensaje());
			sincronizacionResultadoItemRepository.save(entity);
		}
	}

	private List<SincronizacionItemResultadoDTO> mapResultados(List<SincronizacionResultadoItem> entities) {
		List<SincronizacionItemResultadoDTO> resultados = new ArrayList<>();
		for (SincronizacionResultadoItem entity : entities) {
			resultados.add(new SincronizacionItemResultadoDTO(
				entity.getTipoEntidad(),
				entity.getIdempotencyKey(),
				entity.getClientRecordId(),
				entity.getEstado(),
				entity.getServerId(),
				entity.getMensaje()
			));
		}
		return resultados;
	}

	private SincronizacionLoteResumenDTO toResumen(SincronizacionLote lote) {
		return new SincronizacionLoteResumenDTO(
			lote.getLoteId(),
			lote.getDispositivoId(),
			lote.getEstadoLote(),
			lote.getTotalRecibidos(),
			lote.getProcesados(),
			lote.getDuplicados(),
			lote.getConflictos(),
			lote.getErrores(),
			lote.getFechaActualizacion()
		);
	}
}
