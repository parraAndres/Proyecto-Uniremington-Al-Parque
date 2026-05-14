package com.vetsync.app.uniremington.controller;

import com.vetsync.app.uniremington.entity.NotificacionUniremington;
import com.vetsync.app.uniremington.entity.UsuarioUniremington;
import com.vetsync.app.uniremington.repository.NotificacionUniremingtonRepository;
import com.vetsync.app.uniremington.repository.UsuarioUniremingtonRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/uni/notificaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Notifications - Uniremington", description = "Gestión de notificaciones para usuarios de Uniremington")
public class UniNotificacionController {

    private final NotificacionUniremingtonRepository notificacionRepository;
    private final UsuarioUniremingtonRepository usuarioRepository;

    @GetMapping
    @Operation(summary = "Obtener notificaciones", description = "Retorna la lista de notificaciones del usuario autenticado")
    public ResponseEntity<List<NotificacionUniremington>> getNotificaciones(Authentication authentication) {
        String documento = authentication.getName();
        UsuarioUniremington usuario = usuarioRepository.findByDocumento(documento)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return ResponseEntity.ok(notificacionRepository.findByUsuarioOrderByCreatedAtDesc(usuario));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Contar no leídas", description = "Retorna el número de notificaciones no leídas")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication authentication) {
        String documento = authentication.getName();
        UsuarioUniremington usuario = usuarioRepository.findByDocumento(documento)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return ResponseEntity.ok(Map.of("unreadCount", notificacionRepository.countByUsuarioAndLeidaFalse(usuario)));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Marcar como leída", description = "Marca una notificación específica como leída")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        NotificacionUniremington notif = notificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        notif.setLeida(true);
        notificacionRepository.save(notif);
        return ResponseEntity.ok().build();
    }
}
