package com.vetsync.app.repository;

import com.vetsync.app.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByDocumento(String documento);
    
    @org.springframework.data.jpa.repository.Query("SELECT u FROM Usuario u WHERE u.email = :identificador OR u.documento = :identificador")
    Optional<Usuario> findByIdentificador(String identificador);

    boolean existsByEmail(String email);
    boolean existsByDocumento(String documento);

    long countByRol(com.vetsync.app.entity.Usuario.Rol rol);
}
