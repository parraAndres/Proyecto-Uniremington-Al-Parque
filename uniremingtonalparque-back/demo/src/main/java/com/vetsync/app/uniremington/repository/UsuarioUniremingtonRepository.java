package com.vetsync.app.uniremington.repository;

import com.vetsync.app.uniremington.entity.UsuarioUniremington;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UsuarioUniremingtonRepository extends JpaRepository<UsuarioUniremington, String> {

    Optional<UsuarioUniremington> findByDocumento(String documento);
    
    Optional<UsuarioUniremington> findByDocumentoOrEmail(String documento, String email);

    boolean existsByDocumento(String documento);
    
    boolean existsByEmail(String email);

    long countByUpdatedAtAfter(LocalDateTime date);

    long countByRol(String rol);

    java.util.List<UsuarioUniremington> findByFacultadAndRolNot(String facultad, String rol);
}
