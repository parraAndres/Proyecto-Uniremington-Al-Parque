package com.vetsync.app.repository;

import com.vetsync.app.entity.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {
    List<Mascota> findByClienteId(Long clienteId);

    // Dashboard: contar mascotas registradas en un rango de fechas
    long countByFechaRegistroBetween(LocalDate inicio, LocalDate fin);
}
