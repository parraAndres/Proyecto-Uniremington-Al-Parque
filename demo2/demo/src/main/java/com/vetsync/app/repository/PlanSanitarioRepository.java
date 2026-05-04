package com.vetsync.app.repository;

import com.vetsync.app.entity.PlanSanitario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlanSanitarioRepository extends JpaRepository<PlanSanitario, Long> {
    List<PlanSanitario> findByMascotaId(Long mascotaId);
    List<PlanSanitario> findByEstado(PlanSanitario.EstadoVacuna estado);
}
