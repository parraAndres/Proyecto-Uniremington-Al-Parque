package com.vetsync.app.uniremington.repository;

import com.vetsync.app.uniremington.entity.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecursoRepository extends JpaRepository<Recurso, String> {

    List<Recurso> findByFacultadAsociada(String facultadAsociada);

    List<Recurso> findByTipoAporte(String tipoAporte);
}
