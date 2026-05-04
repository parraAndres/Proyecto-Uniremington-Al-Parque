package com.vetsync.app.controller;

import com.vetsync.app.dto.request.RegisterRequest;
import com.vetsync.app.dto.request.UpdatePerfilRequest;
import com.vetsync.app.dto.response.PerfilResponse;
import com.vetsync.app.service.impl.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Registro, perfil y gestión de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario")
    public ResponseEntity<PerfilResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.register(request));
    }

    @GetMapping("/perfil")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener perfil del usuario autenticado")
    public ResponseEntity<PerfilResponse> getPerfil(Authentication auth) {
        return ResponseEntity.ok(usuarioService.getPerfil(auth.getName()));
    }

    @PutMapping("/perfil")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar perfil del usuario autenticado")
    public ResponseEntity<PerfilResponse> updatePerfil(Authentication auth,
            @Valid @RequestBody UpdatePerfilRequest request) {
        return ResponseEntity.ok(usuarioService.updatePerfil(auth.getName(), request));
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos los usuarios (solo ADMIN)")
    public ResponseEntity<List<PerfilResponse>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }
}
