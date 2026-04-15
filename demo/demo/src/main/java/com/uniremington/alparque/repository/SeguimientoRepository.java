package com.uniremington.alparque.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uniremington.alparque.model.Seguimiento;

public interface SeguimientoRepository extends JpaRepository<Seguimiento, UUID> {

	List<Seguimiento> findByCasoId(UUID casoId);
}
