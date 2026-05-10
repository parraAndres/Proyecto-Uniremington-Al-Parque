package com.vetsync.app.repository;

import com.vetsync.app.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioIdOrderByCreadaEnDesc(Long usuarioId);
    List<Notificacion> findByUsuarioIdAndLeidaFalseOrderByCreadaEnDesc(Long usuarioId);
    long countByUsuarioIdAndLeidaFalse(Long usuarioId);

    @Modifying
    @Query("UPDATE Notificacion n SET n.leida = true WHERE n.usuario.id = :uid")
    void marcarTodasLeidas(@Param("uid") Long usuarioId);
}
