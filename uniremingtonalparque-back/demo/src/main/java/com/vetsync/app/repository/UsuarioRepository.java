package com.vetsync.app.repository;

import com.vetsync.app.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);

    long countByRol(com.vetsync.app.entity.Usuario.Rol rol);
}
