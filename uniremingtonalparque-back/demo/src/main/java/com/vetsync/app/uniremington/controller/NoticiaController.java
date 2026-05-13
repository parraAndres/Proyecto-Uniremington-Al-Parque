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
public class NoticiaController {

    private final NoticiaRepository noticiaRepository;

    @GetMapping
    public ResponseEntity<List<Noticia>> getAllNoticias() {
        return ResponseEntity.ok(noticiaRepository.findAllByOrderByFechaPublicacionDesc());
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
