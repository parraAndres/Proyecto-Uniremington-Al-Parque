package com.vetsync.app.service.impl;

import com.vetsync.app.entity.Notificacion;
import com.vetsync.app.entity.Usuario;
import com.vetsync.app.repository.NotificacionRepository;
import com.vetsync.app.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;

    public List<Notificacion> getMisNotificaciones(String email) {
        Usuario u = usuarioRepository.findByEmail(email).orElseThrow();
        return notificacionRepository.findByUsuarioIdOrderByCreadaEnDesc(u.getId());
    }

    public long contarNoLeidas(String email) {
        Usuario u = usuarioRepository.findByEmail(email).orElseThrow();
        return notificacionRepository.countByUsuarioIdAndLeidaFalse(u.getId());
    }

    @Transactional
    public void marcarTodasLeidas(String email) {
        Usuario u = usuarioRepository.findByEmail(email).orElseThrow();
        notificacionRepository.marcarTodasLeidas(u.getId());
    }

    @Transactional
    public void marcarLeida(Long id) {
        notificacionRepository.findById(id).ifPresent(n -> { n.setLeida(true); notificacionRepository.save(n); });
    }
}
