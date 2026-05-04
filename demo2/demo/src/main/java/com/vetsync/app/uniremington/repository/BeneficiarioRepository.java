package com.vetsync.app.uniremington.repository;

import com.vetsync.app.uniremington.entity.Beneficiario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @deprecated Use {@link BeneficiarioUniRepository} para el módulo Uniremington al Parque.
 *             Este repositorio se mantiene por compatibilidad con DomainService.
 */
@Deprecated
public interface BeneficiarioRepository extends JpaRepository<Beneficiario, String> {
    Optional<Beneficiario> findByDocumento(String documento);
}
