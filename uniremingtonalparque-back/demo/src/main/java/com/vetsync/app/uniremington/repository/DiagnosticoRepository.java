package com.vetsync.app.uniremington.repository;

import com.vetsync.app.uniremington.entity.Diagnostico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiagnosticoRepository extends JpaRepository<Diagnostico, String> {

    List<Diagnostico> findByBeneficiarioId(String beneficiarioId);
}
