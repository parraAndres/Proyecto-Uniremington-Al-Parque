package com.uniremington.alparque.repository;

import com.uniremington.alparque.model.Beneficiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository("alparqueBeneficiarioRepository")
public interface BeneficiarioRepository extends JpaRepository<Beneficiario, UUID> {

    Optional<Beneficiario> findByNumeroDocumento(String numeroDocumento);

    boolean existsByNumeroDocumento(String numeroDocumento);
}
