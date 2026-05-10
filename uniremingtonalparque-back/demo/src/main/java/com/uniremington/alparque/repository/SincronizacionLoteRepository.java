package com.uniremington.alparque.repository;

import com.uniremington.alparque.model.SincronizacionLote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SincronizacionLoteRepository extends JpaRepository<SincronizacionLote, Long> {

    Optional<SincronizacionLote> findByLoteIdAndDispositivoId(String loteId, String dispositivoId);

    Page<SincronizacionLote> findByDispositivoIdAndEstadoLote(
            String dispositivoId,
            com.uniremington.alparque.model.enums.EstadoLote estadoLote,
            Pageable pageable);

    Page<SincronizacionLote> findByDispositivoIdAndEstadoLoteAndFechaLoteBetween(
            String dispositivoId,
            com.uniremington.alparque.model.enums.EstadoLote estadoLote,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Pageable pageable);
}
