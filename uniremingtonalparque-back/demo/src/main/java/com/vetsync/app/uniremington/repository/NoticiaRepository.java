package com.vetsync.app.uniremington.repository;

import com.vetsync.app.uniremington.entity.Noticia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticiaRepository extends JpaRepository<Noticia, String> {
    List<Noticia> findAllByOrderByFechaPublicacionDesc();
}
