package com.vetsync.app.uniremington.controller;

import com.vetsync.app.uniremington.entity.UsuarioUniremington;
import com.vetsync.app.uniremington.repository.UsuarioUniremingtonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("uniUsuarioController")
@RequestMapping("/uni/usuarios")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH, RequestMethod.OPTIONS})
@RequiredArgsConstructor
public class UniUsuarioController {

    private final UsuarioUniremingtonRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<UsuarioUniremington>> getAllUsuarios() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioUniremington> updateUsuario(@PathVariable String id, @RequestBody UsuarioUniremington updatedData) {
        return usuarioRepository.findById(id).map(u -> {
            u.setNombreCompleto(updatedData.getNombreCompleto());
            u.setEmail(updatedData.getEmail());
            u.setFacultad(updatedData.getFacultad());
            u.setPrograma(updatedData.getPrograma());
            u.setRol(updatedData.getRol());
            u.setGenero(updatedData.getGenero());
            // No actualizamos password aquí por seguridad en este endpoint simple
            return ResponseEntity.ok(usuarioRepository.save(u));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<UsuarioUniremington> toggleStatus(@PathVariable String id) {
        return usuarioRepository.findById(id).map(u -> {
            u.setActivo(!u.isActivo());
            return ResponseEntity.ok(usuarioRepository.save(u));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{idOrEmail}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable String idOrEmail) {
        usuarioRepository.findByDocumentoOrEmail(idOrEmail, idOrEmail)
                .ifPresent(usuarioRepository::delete);
        return ResponseEntity.noContent().build();
    }
}
