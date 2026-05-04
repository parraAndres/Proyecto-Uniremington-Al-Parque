package com.vetsync.app.repository;

import com.vetsync.app.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByMascotaId(Long mascotaId);
    List<Cita> findByVeterinarioId(Long veterinarioId);
    List<Cita> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);
    List<Cita> findByEstado(Cita.EstadoCita estado);

    // Dashboard: contar citas completadas sin historia clínica por veterinario
    @Query("SELECT COUNT(c) FROM Cita c WHERE c.veterinario.id = :vetId " +
           "AND c.estado = 'COMPLETADA' AND c.historiaClinica IS NULL")
    long countCompletadasSinHistoria(@Param("vetId") Long veterinarioId);
}

