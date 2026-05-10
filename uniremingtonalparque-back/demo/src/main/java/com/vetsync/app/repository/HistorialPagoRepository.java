package com.vetsync.app.repository;

import com.vetsync.app.entity.HistorialPago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistorialPagoRepository extends JpaRepository<HistorialPago, Long> {
    List<HistorialPago> findByFacturaId(Long facturaId);
}
