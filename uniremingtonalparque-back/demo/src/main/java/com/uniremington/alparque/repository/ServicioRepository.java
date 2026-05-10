package com.uniremington.alparque.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para servicios sociales prestados en las jornadas.
 * Placeholder: ampliar según el modelo ServicioSocial del módulo alparque.
 */
@Repository
public interface ServicioRepository extends JpaRepository<com.uniremington.alparque.model.Beneficiario, java.util.UUID> {
    // Pendiente: se vinculará a una entidad ServicioSocial propia
    // cuando se modele el catálogo de servicios del módulo alparque.
}
