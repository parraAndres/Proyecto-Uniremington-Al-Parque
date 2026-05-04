package com.vetsync.app.service.impl;

import com.vetsync.app.dto.response.DashboardResponse;
import com.vetsync.app.entity.*;
import com.vetsync.app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final ClienteRepository clienteRepository;
    private final MascotaRepository mascotaRepository;
    private final CitaRepository citaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final com.vetsync.app.repository.FacturaRepository facturaRepository;
    private final com.vetsync.app.repository.FormulaMedicaRepository formulaRepository;

    // ── ADMIN ────────────────────────────────────────────────
    public DashboardResponse.MetricasAdmin getMetricasAdmin() {
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicioMes = hoy.withDayOfMonth(1).atStartOfDay();
        LocalDateTime finMes    = hoy.atTime(LocalTime.MAX);

        long totalClientes = clienteRepository.count();
        long totalMascotas = mascotaRepository.count();
        long facturasPendientes = facturaRepository.countByEstado(Factura.EstadoFactura.PENDIENTE);
        long citasHoy = citaRepository.findByFechaHoraBetween(hoy.atStartOfDay(), hoy.atTime(LocalTime.MAX)).size();
        long stockBajo = productoRepository.findByStockActualLessThanEqualStockMinimo().size();

        BigDecimal facturacionMes = facturaRepository
                .sumTotalByEstadoAndFechaEmisionBetween(Factura.EstadoFactura.PAGADA, hoy.withDayOfMonth(1), hoy)
                .orElse(BigDecimal.ZERO);

        // Ingresos últimos 7 días
        List<DashboardResponse.IngresosDia> ingresos = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 6; i >= 0; i--) {
            LocalDate fecha = hoy.minusDays(i);
            BigDecimal total = facturaRepository
                    .sumTotalByEstadoAndFechaEmisionBetween(Factura.EstadoFactura.PAGADA, fecha, fecha)
                    .orElse(BigDecimal.ZERO);
            ingresos.add(DashboardResponse.IngresosDia.builder()
                    .fecha(fecha.format(fmt))
                    .total(total)
                    .build());
        }

        // Distribución de roles
        List<DashboardResponse.DistribucionRol> distribucion = Arrays.stream(Usuario.Rol.values())
                .map(rol -> DashboardResponse.DistribucionRol.builder()
                        .rol(rol.name())
                        .cantidad(usuarioRepository.countByRol(rol))
                        .build())
                .collect(Collectors.toList());

        return DashboardResponse.MetricasAdmin.builder()
                .totalClientes(totalClientes)
                .totalMascotas(totalMascotas)
                .facturacionMes(facturacionMes)
                .facturasPendientes(facturasPendientes)
                .citasHoy(citasHoy)
                .productosStockBajo(stockBajo)
                .ingresosUltimos7Dias(ingresos)
                .distribucionRoles(distribucion)
                .build();
    }

    // ── VETERINARIO ──────────────────────────────────────────
    public DashboardResponse.MetricasVeterinario getMetricasVeterinario(Long veterinarioId) {
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicioHoy = hoy.atStartOfDay();
        LocalDateTime finHoy    = hoy.atTime(LocalTime.MAX);
        LocalDateTime inicioMes = hoy.withDayOfMonth(1).atStartOfDay();

        long programadasHoy = citaRepository
                .findByVeterinarioId(veterinarioId).stream()
                .filter(c -> !c.getFechaHora().isBefore(inicioHoy) && !c.getFechaHora().isAfter(finHoy))
                .count();

        long completadasMes = citaRepository
                .findByVeterinarioId(veterinarioId).stream()
                .filter(c -> c.getEstado() == Cita.EstadoCita.COMPLETADA
                        && !c.getFechaHora().isBefore(inicioMes))
                .count();

        // Próximas citas del día
        List<DashboardResponse.CitaResumen> proximasCitas = citaRepository
                .findByFechaHoraBetween(inicioHoy, finHoy).stream()
                .filter(c -> c.getVeterinario().getId().equals(veterinarioId))
                .map(this::toCitaResumen)
                .collect(Collectors.toList());

        // Citas completadas sin historia clínica = historias pendientes
        long historiasPendientes = citaRepository.countCompletadasSinHistoria(veterinarioId);

        return DashboardResponse.MetricasVeterinario.builder()
                .citasProgramadasHoy(programadasHoy)
                .citasCompletadasMes(completadasMes)
                .historiasPendientes(historiasPendientes)
                .formulasEmitidas(formulaRepository.countByVeterinarioId(veterinarioId))
                .proximasCitas(proximasCitas)
                .build();
    }

    // ── FARMACEUTICO ─────────────────────────────────────────
    public DashboardResponse.MetricasFarmaceutico getMetricasFarmaceutico() {
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicioHoy = hoy.atStartOfDay();
        LocalDateTime finHoy    = hoy.atTime(LocalTime.MAX);

        long pendientes   = formulaRepository.countByEstado(FormulaMedica.EstadoFormula.PENDIENTE);
        long dispensadas  = formulaRepository.countByEstadoAndFechaDispensacionBetween(
                FormulaMedica.EstadoFormula.DISPENSADA, inicioHoy, finHoy);
        long totalProductos = productoRepository.count();

        List<Producto> stockBajo = productoRepository.findByStockActualLessThanEqualStockMinimo();
        List<DashboardResponse.AlertaStock> alertas = stockBajo.stream()
                .map(p -> DashboardResponse.AlertaStock.builder()
                        .productoId(p.getId())
                        .codigo(p.getCodigo())
                        .nombre(p.getNombre())
                        .stockActual(p.getStockActual())
                        .stockMinimo(p.getStockMinimo())
                        .build())
                .collect(Collectors.toList());

        return DashboardResponse.MetricasFarmaceutico.builder()
                .formulasPendientes(pendientes)
                .formulasDispensadasHoy(dispensadas)
                .productosStockBajo(stockBajo.size())
                .totalProductos(totalProductos)
                .alertasStock(alertas)
                .build();
    }

    // ── AUXILIAR ─────────────────────────────────────────────
    public DashboardResponse.MetricasAuxiliar getMetricasAuxiliar() {
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicioHoy = hoy.atStartOfDay();
        LocalDateTime finHoy    = hoy.atTime(LocalTime.MAX);
        LocalDate inicioMes = hoy.withDayOfMonth(1);

        long citasHoy = citaRepository.findByFechaHoraBetween(inicioHoy, finHoy).size();

        // Contar clientes y mascotas registrados este mes
        long clientesMes = clienteRepository.countByFechaRegistroBetween(inicioMes, hoy);
        long mascotasMes = mascotaRepository.countByFechaRegistroBetween(inicioMes, hoy);

        List<DashboardResponse.CitaResumen> proximasCitas = citaRepository
                .findByFechaHoraBetween(inicioHoy, finHoy).stream()
                .map(this::toCitaResumen)
                .collect(Collectors.toList());

        return DashboardResponse.MetricasAuxiliar.builder()
                .citasHoy(citasHoy)
                .clientesRegistradosMes(clientesMes)
                .mascotasRegistradasMes(mascotasMes)
                .proximasCitas(proximasCitas)
                .build();
    }

    // ── Helper ────────────────────────────────────────────────
    private DashboardResponse.CitaResumen toCitaResumen(Cita c) {
        return DashboardResponse.CitaResumen.builder()
                .id(c.getId())
                .mascotaNombre(c.getMascota().getNombre())
                .clienteNombre(c.getMascota().getCliente().getNombre())
                .veterinarioNombre(c.getVeterinario().getNombre())
                .fechaHora(c.getFechaHora())
                .estado(c.getEstado())
                .motivo(c.getMotivo())
                .build();
    }
}
