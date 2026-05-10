package com.vetsync.app.controller;

import com.vetsync.app.entity.Notificacion;
import com.vetsync.app.service.impl.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notificaciones")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "Centro de notificaciones del usuario")
@SecurityRequirement(name = "bearerAuth")
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping
    @Operation(summary = "Listar mis notificaciones")
    public ResponseEntity<List<Notificacion>> getMias(Authentication auth) {
        return ResponseEntity.ok(notificacionService.getMisNotificaciones(auth.getName()));
    }

    @GetMapping("/no-leidas/count")
    @Operation(summary = "Contar notificaciones no leídas")
    public ResponseEntity<Map<String, Long>> countNoLeidas(Authentication auth) {
        return ResponseEntity.ok(Map.of("count", notificacionService.contarNoLeidas(auth.getName())));
    }

    @PutMapping("/leer-todas")
    @Operation(summary = "Marcar todas como leídas")
    public ResponseEntity<Void> leerTodas(Authentication auth) {
        notificacionService.marcarTodasLeidas(auth.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/leer")
    @Operation(summary = "Marcar una notificación como leída")
    public ResponseEntity<Void> leerUna(@PathVariable Long id) {
        notificacionService.marcarLeida(id);
        return ResponseEntity.noContent().build();
    }
}
