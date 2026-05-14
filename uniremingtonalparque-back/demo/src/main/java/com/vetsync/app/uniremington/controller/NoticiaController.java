package com.vetsync.app.uniremington.controller;

import com.vetsync.app.uniremington.entity.Noticia;
import com.vetsync.app.uniremington.repository.NoticiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/noticias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NoticiaController {

    private final NoticiaRepository noticiaRepository;

    @GetMapping
    public ResponseEntity<List<Noticia>> getAllNoticias() {
        return ResponseEntity.ok(noticiaRepository.findAllByOrderByFechaPublicacionDesc());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Noticia> getNoticiaById(@PathVariable String id) {
        System.out.println("Buscando noticia con ID: " + id);
        return noticiaRepository.findById(id)
                .map(n -> {
                    System.out.println("Noticia encontrada: " + n.getTitulo());
                    return ResponseEntity.ok(n);
                })
                .orElseGet(() -> {
                    System.out.println("Noticia NO encontrada con ID: " + id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<Noticia> createNoticia(@RequestBody Noticia noticia) {
        return ResponseEntity.ok(noticiaRepository.save(noticia));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoticia(@PathVariable String id) {
        noticiaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
