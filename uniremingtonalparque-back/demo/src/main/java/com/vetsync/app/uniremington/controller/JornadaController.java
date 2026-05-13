package com.vetsync.app.uniremington.controller;

import com.vetsync.app.uniremington.entity.Jornada;
import com.vetsync.app.uniremington.entity.UsuarioUniremington;
import com.vetsync.app.uniremington.repository.JornadaRepository;
import com.vetsync.app.uniremington.repository.UsuarioUniremingtonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/uni/jornadas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JornadaController {

    private final JornadaRepository jornadaRepository;
    private final UsuarioUniremingtonRepository usuarioRepository;

    @GetMapping
    public List<Jornada> getAll() {
        return jornadaRepository.findAll();
    }

    @PostMapping
    public Jornada create(@RequestBody Jornada jornada) {
        return jornadaRepository.save(jornada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Jornada> update(@PathVariable Long id, @RequestBody Jornada details) {
        return jornadaRepository.findById(id).map(j -> {
            j.setNombre(details.getNombre());
            j.setFecha(details.getFecha());
            j.setMunicipio(details.getMunicipio());
            j.setVereda(details.getVereda());
            j.setBarrio(details.getBarrio());
            j.setDescripcion(details.getDescripcion());
            j.setEstado(details.getEstado());
            return ResponseEntity.ok(jornadaRepository.save(j));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/asignar/{usuarioId}")
    public ResponseEntity<Jornada> asignarPersonal(@PathVariable Long id, @PathVariable String usuarioId) {
        return jornadaRepository.findById(id).flatMap(j -> 
            usuarioRepository.findById(usuarioId).map(u -> {
                if (!j.getPersonalAsignado().contains(u)) {
                    j.getPersonalAsignado().add(u);
                }
                return ResponseEntity.ok(jornadaRepository.save(j));
            })
        ).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/quitar/{usuarioId}")
    public ResponseEntity<Jornada> quitarPersonal(@PathVariable Long id, @PathVariable String usuarioId) {
        return jornadaRepository.findById(id).map(j -> {
            j.getPersonalAsignado().removeIf(u -> u.getId().equals(usuarioId));
            return ResponseEntity.ok(jornadaRepository.save(j));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        jornadaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
