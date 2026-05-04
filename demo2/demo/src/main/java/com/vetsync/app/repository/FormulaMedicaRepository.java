package com.vetsync.app.repository;

import com.vetsync.app.entity.FormulaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface FormulaMedicaRepository extends JpaRepository<FormulaMedica, Long> {

    long countByEstado(FormulaMedica.EstadoFormula estado);

    long countByVeterinarioId(Long veterinarioId);

    long countByEstadoAndFechaDispensacionBetween(
            FormulaMedica.EstadoFormula estado,
            LocalDateTime inicio,
            LocalDateTime fin);

    List<FormulaMedica> findByEstado(FormulaMedica.EstadoFormula estado);
}
