package com.vetsync.app.uniremington.repository;

import com.vetsync.app.uniremington.entity.Academico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcademicoRepository extends JpaRepository<Academico, String> {

    List<Academico> findByFacultad(String facultad);

    List<Academico> findByPrograma(String programa);
}
