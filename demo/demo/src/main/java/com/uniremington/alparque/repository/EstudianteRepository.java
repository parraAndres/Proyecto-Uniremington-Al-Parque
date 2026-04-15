package com.uniremington.alparque.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uniremington.alparque.model.Estudiante;

public interface EstudianteRepository extends JpaRepository<Estudiante, UUID> {

	boolean existsByNumeroDocumento(String numeroDocumento);

	boolean existsByNumeroDocumentoAndIdNot(String numeroDocumento, UUID id);
}
