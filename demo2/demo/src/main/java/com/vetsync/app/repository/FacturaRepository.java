package com.vetsync.app.repository;

import com.vetsync.app.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FacturaRepository extends JpaRepository<Factura, Long> {

    long countByEstado(Factura.EstadoFactura estado);

    List<Factura> findByClienteId(Long clienteId);

    @Query("SELECT COALESCE(SUM(f.total), 0) FROM Factura f " +
           "WHERE f.estado = :estado AND f.fechaEmision BETWEEN :inicio AND :fin")
    Optional<BigDecimal> sumTotalByEstadoAndFechaEmisionBetween(
            @Param("estado") Factura.EstadoFactura estado,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin);
}
