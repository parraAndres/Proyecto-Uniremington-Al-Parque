package com.vetsync.app.uniremington.repository;

import com.vetsync.app.uniremington.entity.Seguimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeguimientoRepository extends JpaRepository<Seguimiento, String> {

    List<Seguimiento> findByBeneficiarioId(String beneficiarioId);
}
