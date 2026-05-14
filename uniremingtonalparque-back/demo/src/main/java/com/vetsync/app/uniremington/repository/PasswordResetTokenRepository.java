package com.vetsync.app.uniremington.repository;

import com.vetsync.app.uniremington.entity.PasswordResetToken;
import com.vetsync.app.uniremington.entity.UsuarioUniremington;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUsuario(UsuarioUniremington usuario);
}
