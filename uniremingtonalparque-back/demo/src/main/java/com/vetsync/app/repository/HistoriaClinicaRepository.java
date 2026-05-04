package com.vetsync.app.repository;

import com.vetsync.app.entity.HistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinica, Long> {
    Optional<HistoriaClinica> findByCitaId(Long citaId);
    List<HistoriaClinica> findByCita_MascotaId(Long mascotaId);
}
