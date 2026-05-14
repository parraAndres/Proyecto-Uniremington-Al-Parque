package com.vetsync.app.uniremington.repository;

import com.vetsync.app.uniremington.entity.NotificacionUniremington;
import com.vetsync.app.uniremington.entity.UsuarioUniremington;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionUniremingtonRepository extends JpaRepository<NotificacionUniremington, Long> {
    List<NotificacionUniremington> findByUsuarioOrderByCreatedAtDesc(UsuarioUniremington usuario);
    long countByUsuarioAndLeidaFalse(UsuarioUniremington usuario);
}
