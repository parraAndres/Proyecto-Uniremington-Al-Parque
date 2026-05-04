package com.vetsync.app.repository;

import com.vetsync.app.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByDocumento(String documento);
    Optional<Cliente> findByEmail(String email);
    boolean existsByDocumento(String documento);

    // Dashboard: contar clientes registrados en un rango de fechas
    long countByFechaRegistroBetween(LocalDate inicio, LocalDate fin);
}
