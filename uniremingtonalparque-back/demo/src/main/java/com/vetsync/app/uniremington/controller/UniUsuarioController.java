package com.vetsync.app.uniremington.controller;

import com.vetsync.app.entity.Usuario;
import com.vetsync.app.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("uniUsuarioController")
@RequestMapping("/uni/usuarios")
@RequiredArgsConstructor
public class UniUsuarioController {

    private final UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    @DeleteMapping("/{idOrEmail}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable String idOrEmail) {
        usuarioRepository.findByIdentificador(idOrEmail)
                .ifPresent(usuarioRepository::delete);
        return ResponseEntity.noContent().build();
    }
}
