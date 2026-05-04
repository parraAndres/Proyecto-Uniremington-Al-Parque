package com.vetsync.app.uniremington.repository;

import com.vetsync.app.uniremington.entity.Beneficiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BeneficiarioUniRepository extends JpaRepository<Beneficiario, String> {

    Optional<Beneficiario> findByDocumento(String documento);

    boolean existsByDocumento(String documento);
}
