package com.uniremington.alparque.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.uniremington.alparque.model.SincronizacionLote;

public interface SincronizacionLoteRepository extends JpaRepository<SincronizacionLote, UUID> {

    Optional<SincronizacionLote> findByLoteIdAndDispositivoId(String loteId, String dispositivoId);

    Page<SincronizacionLote> findByDispositivoId(String dispositivoId, Pageable pageable);

    Page<SincronizacionLote> findByDispositivoIdAndEstadoLote(String dispositivoId, String estadoLote, Pageable pageable);

        Page<SincronizacionLote> findByDispositivoIdAndFechaActualizacionBetween(
            String dispositivoId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Pageable pageable);

        Page<SincronizacionLote> findByDispositivoIdAndEstadoLoteAndFechaActualizacionBetween(
            String dispositivoId,
            String estadoLote,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Pageable pageable);
}