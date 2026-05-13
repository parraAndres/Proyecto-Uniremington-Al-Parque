package com.vetsync.app.uniremington.controller;

import com.vetsync.app.uniremington.entity.UsuarioUniremington;
import com.vetsync.app.uniremington.repository.UsuarioUniremingtonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/uni/usuarios")
@RequiredArgsConstructor
public class UniUsuarioController {

    private final UsuarioUniremingtonRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<UsuarioUniremington>> getAllUsuarios() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    @DeleteMapping("/{idOrEmail}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable String idOrEmail) {
        usuarioRepository.findByDocumentoOrEmail(idOrEmail, idOrEmail)
                .ifPresent(usuarioRepository::delete);
        return ResponseEntity.noContent().build();
    }
}
