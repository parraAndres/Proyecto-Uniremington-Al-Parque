package com.vetsync.app.uniremington.repository;

import com.vetsync.app.uniremington.entity.ParticipacionAcademica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ParticipacionAcademicaRepository extends JpaRepository<ParticipacionAcademica, Long> {

    @Query("""
        select p.facultad, coalesce(sum(p.horasReportadas), 0)
        from ParticipacionAcademica p
        where (:inicio is null or p.fechaActividad >= :inicio)
          and (:fin is null or p.fechaActividad <= :fin)
        group by p.facultad
        """)
    List<Object[]> horasPorFacultad(LocalDate inicio, LocalDate fin);
}
