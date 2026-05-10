package com.uniremington.alparque.repository;

import com.uniremington.alparque.model.SincronizacionEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SincronizacionEventoRepository extends JpaRepository<SincronizacionEvento, Long> {

    Optional<SincronizacionEvento> findByIdempotencyKey(String idempotencyKey);

    /**
     * Recupera el evento más reciente para una entidad dada,
     * ordenando primero por fechaCliente y luego por fechaRegistro como desempate.
     */
    @Query("""
        SELECT e FROM SincronizacionEvento e
        WHERE e.tipoEntidad = :tipoEntidad
          AND e.entidadId   = :entidadId
        ORDER BY e.fechaCliente DESC, e.fechaRegistro DESC
        LIMIT 1
        """)
    Optional<SincronizacionEvento> findTopByTipoEntidadAndEntidadIdOrderByFechaClienteDescFechaRegistroDesc(
            @Param("tipoEntidad") String tipoEntidad,
            @Param("entidadId")   UUID entidadId);
}
