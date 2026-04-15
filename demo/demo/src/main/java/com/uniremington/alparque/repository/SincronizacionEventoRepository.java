package com.uniremington.alparque.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uniremington.alparque.model.SincronizacionEvento;

public interface SincronizacionEventoRepository extends JpaRepository<SincronizacionEvento, UUID> {

    Optional<SincronizacionEvento> findByIdempotencyKey(String idempotencyKey);

    Optional<SincronizacionEvento> findTopByTipoEntidadAndEntidadIdOrderByFechaClienteDescFechaRegistroDesc(
            String tipoEntidad,
            UUID entidadId);
}