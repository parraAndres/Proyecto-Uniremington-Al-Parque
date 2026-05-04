package com.vetsync.app.uniremington.repository;

import com.vetsync.app.uniremington.entity.UsuarioUniremington;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioUniremingtonRepository extends JpaRepository<UsuarioUniremington, String> {

    Optional<UsuarioUniremington> findByDocumento(String documento);

    boolean existsByDocumento(String documento);
}
