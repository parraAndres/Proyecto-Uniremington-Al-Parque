package com.uniremington.alparque.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uniremington.alparque.dto.response.IndicadorMensualDTO;
import com.uniremington.alparque.dto.response.IndicadorResponseDTO;
import com.uniremington.alparque.dto.response.IndicadorTendenciaFacultadResponseDTO;
import com.uniremington.alparque.dto.response.IndicadorTendenciaResponseDTO;
import com.uniremington.alparque.model.enums.Facultad;
import com.uniremington.alparque.repository.BeneficiarioRepository;
import com.uniremington.alparque.repository.ServicioRepository;
import com.uniremington.alparque.service.IndicadorService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndicadorServiceImpl implements IndicadorService {

	private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

	private final BeneficiarioRepository beneficiarioRepository;
	private final ServicioRepository servicioRepository;

	@Override
	public IndicadorResponseDTO getResumen() {
		return getResumen(null, null);
	}

	@Override
	public IndicadorResponseDTO getResumen(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
		boolean filtrarPorFecha = fechaInicio != null && fechaFin != null;
		validateRangoFechas(fechaInicio, fechaFin, filtrarPorFecha);

		long numeroBeneficiarios = filtrarPorFecha
			? beneficiarioRepository.countByFechaRegistroBetween(fechaInicio, fechaFin)
			: beneficiarioRepository.count();

		long numeroServicios = filtrarPorFecha
			? servicioRepository.countByFechaAtencionBetween(fechaInicio, fechaFin)
			: servicioRepository.count();

		Map<String, Long> serviciosPorFacultad = filtrarPorFecha
			? toMap(servicioRepository.countServiciosByFacultadBetween(fechaInicio, fechaFin))
			: toMap(servicioRepository.countServiciosByFacultad());

		Map<String, Long> coberturaTerritorialPorMunicipio = filtrarPorFecha
			? toMap(beneficiarioRepository.countBeneficiariosByMunicipioBetween(fechaInicio, fechaFin))
			: toMap(beneficiarioRepository.countBeneficiariosByMunicipio());

		Map<String, Long> serviciosPorResultado = filtrarPorFecha
			? toMap(servicioRepository.countServiciosByResultadoBetween(fechaInicio, fechaFin))
			: toMap(servicioRepository.countServiciosByResultado());

		return new IndicadorResponseDTO(
			numeroBeneficiarios,
			numeroServicios,
			serviciosPorFacultad,
			coberturaTerritorialPorMunicipio,
			serviciosPorResultado
		);
	}

	@Override
	public IndicadorTendenciaResponseDTO getTendenciaMensual(LocalDate fechaInicio, LocalDate fechaFin) {
		if ((fechaInicio == null) != (fechaFin == null)) {
			throw new IllegalArgumentException("Debe enviar fechaInicio y fechaFin juntas para la tendencia mensual");
		}

		LocalDate startDate = fechaInicio != null ? fechaInicio : LocalDate.now().minusMonths(11).withDayOfMonth(1);
		LocalDate endDate = fechaFin != null ? fechaFin : LocalDate.now();

		if (startDate.isAfter(endDate)) {
			throw new IllegalArgumentException("fechaInicio no puede ser mayor que fechaFin");
		}

		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

		List<IndicadorMensualDTO> serie = buildSerieMensual(startDate, endDate, startDateTime, endDateTime);

		return new IndicadorTendenciaResponseDTO(startDate.toString(), endDate.toString(), serie);
	}

	@Override
	public List<IndicadorMensualDTO> getTendenciaMensual() {
		return getTendenciaMensual(null, null).getSerieMensual();
	}

	@Override
	public IndicadorTendenciaFacultadResponseDTO getTendenciaMensualPorFacultad(LocalDate fechaInicio,
			LocalDate fechaFin) {
		if ((fechaInicio == null) != (fechaFin == null)) {
			throw new IllegalArgumentException("Debe enviar fechaInicio y fechaFin juntas para la tendencia mensual por facultad");
		}

		LocalDate startDate = fechaInicio != null ? fechaInicio : LocalDate.now().minusMonths(11).withDayOfMonth(1);
		LocalDate endDate = fechaFin != null ? fechaFin : LocalDate.now();

		if (startDate.isAfter(endDate)) {
			throw new IllegalArgumentException("fechaInicio no puede ser mayor que fechaFin");
		}

		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

		List<String> meses = buildMeses(startDate, endDate);
		Map<String, List<Long>> seriesPorFacultad = buildSeriePorFacultad(startDate, endDate, startDateTime, endDateTime);

		return new IndicadorTendenciaFacultadResponseDTO(startDate.toString(), endDate.toString(), meses,
				seriesPorFacultad);
	}

	private void validateRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin, boolean filtrarPorFecha) {
		if (!filtrarPorFecha && (fechaInicio != null || fechaFin != null)) {
			throw new IllegalArgumentException("Debe enviar fechaInicio y fechaFin juntas para filtrar por rango");
		}

		if (filtrarPorFecha && fechaInicio != null && fechaFin != null && fechaInicio.isAfter(fechaFin)) {
			throw new IllegalArgumentException("fechaInicio no puede ser mayor que fechaFin");
		}
	}

	private Map<String, Long> toMap(List<Object[]> rows) {
		Map<String, Long> result = new LinkedHashMap<>();
		for (Object[] row : rows) {
			if (row == null || row.length < 2 || row[0] == null || row[1] == null) {
				continue;
			}
			result.put(String.valueOf(row[0]), ((Number) row[1]).longValue());
		}
		return result;
	}

	private List<IndicadorMensualDTO> buildSerieMensual(LocalDate fechaInicio,
			LocalDate fechaFin,
			LocalDateTime fechaInicioDateTime,
			LocalDateTime fechaFinDateTime) {

		Map<YearMonth, Long> beneficiariosPorMes = toYearMonthMap(
			beneficiarioRepository.countBeneficiariosByMesBetween(fechaInicioDateTime, fechaFinDateTime));

		Map<YearMonth, Long> serviciosPorMes = toYearMonthMap(
			servicioRepository.countServiciosByMesBetween(fechaInicioDateTime, fechaFinDateTime));

		List<IndicadorMensualDTO> serie = new ArrayList<>();
		YearMonth start = YearMonth.from(fechaInicio);
		YearMonth end = YearMonth.from(fechaFin);

		for (YearMonth current = start; !current.isAfter(end); current = current.plusMonths(1)) {
			long totalBeneficiarios = beneficiariosPorMes.getOrDefault(current, 0L);
			long totalServicios = serviciosPorMes.getOrDefault(current, 0L);
			serie.add(new IndicadorMensualDTO(current.format(MONTH_FORMATTER), totalBeneficiarios, totalServicios));
		}

		return serie;
	}

	private Map<YearMonth, Long> toYearMonthMap(List<Object[]> rows) {
		Map<YearMonth, Long> result = new LinkedHashMap<>();
		for (Object[] row : rows) {
			if (row == null || row.length < 3 || row[0] == null || row[1] == null || row[2] == null) {
				continue;
			}

			int year = ((Number) row[0]).intValue();
			int month = ((Number) row[1]).intValue();
			long total = ((Number) row[2]).longValue();
			result.put(YearMonth.of(year, month), total);
		}
		return result;
	}

	private List<String> buildMeses(LocalDate fechaInicio, LocalDate fechaFin) {
		List<String> meses = new ArrayList<>();
		YearMonth start = YearMonth.from(fechaInicio);
		YearMonth end = YearMonth.from(fechaFin);

		for (YearMonth current = start; !current.isAfter(end); current = current.plusMonths(1)) {
			meses.add(current.format(MONTH_FORMATTER));
		}

		return meses;
	}

	private Map<String, List<Long>> buildSeriePorFacultad(LocalDate fechaInicio,
			LocalDate fechaFin,
			LocalDateTime fechaInicioDateTime,
			LocalDateTime fechaFinDateTime) {

		List<Object[]> rows = servicioRepository.countServiciosByMesYFacultadBetween(fechaInicioDateTime, fechaFinDateTime);
		Map<YearMonth, Map<String, Long>> acumulado = toYearMonthFacultadMap(rows);

		Map<String, List<Long>> series = new LinkedHashMap<>();
		List<String> facultades = Arrays.stream(Facultad.values()).map(Enum::name).toList();
		for (String facultad : facultades) {
			series.put(facultad, new ArrayList<>());
		}

		YearMonth start = YearMonth.from(fechaInicio);
		YearMonth end = YearMonth.from(fechaFin);
		for (YearMonth current = start; !current.isAfter(end); current = current.plusMonths(1)) {
			Map<String, Long> facultadesDelMes = acumulado.getOrDefault(current, Map.of());
			for (String facultad : facultades) {
				series.get(facultad).add(facultadesDelMes.getOrDefault(facultad, 0L));
			}
		}

		return series;
	}

	private Map<YearMonth, Map<String, Long>> toYearMonthFacultadMap(List<Object[]> rows) {
		Map<YearMonth, Map<String, Long>> result = new LinkedHashMap<>();
		for (Object[] row : rows) {
			if (row == null || row.length < 4 || row[0] == null || row[1] == null || row[2] == null || row[3] == null) {
				continue;
			}

			int year = ((Number) row[0]).intValue();
			int month = ((Number) row[1]).intValue();
			String facultad = String.valueOf(row[2]);
			long total = ((Number) row[3]).longValue();

			YearMonth key = YearMonth.of(year, month);
			result.computeIfAbsent(key, ignored -> new LinkedHashMap<>()).put(facultad, total);
		}
		return result;
	}
}
