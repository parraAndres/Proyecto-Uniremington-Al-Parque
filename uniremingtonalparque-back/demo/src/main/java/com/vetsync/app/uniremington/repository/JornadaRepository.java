package com.vetsync.app.uniremington.repository;

import com.vetsync.app.uniremington.entity.Jornada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JornadaRepository extends JpaRepository<Jornada, Long> {
    List<Jornada> findByFechaAfterOrderByFechaAsc(LocalDate date);
    List<Jornada> findByMunicipio(String municipio);
}
