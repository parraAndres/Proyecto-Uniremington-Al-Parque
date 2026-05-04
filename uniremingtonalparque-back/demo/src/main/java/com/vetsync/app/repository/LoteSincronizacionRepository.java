package com.vetsync.app.repository;

import com.vetsync.app.entity.LoteSincronizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LoteSincronizacionRepository extends JpaRepository<LoteSincronizacion, Long> {
    Optional<LoteSincronizacion> findByClaveIdempotencia(String claveIdempotencia);
    boolean existsByClaveIdempotencia(String claveIdempotencia);
}
