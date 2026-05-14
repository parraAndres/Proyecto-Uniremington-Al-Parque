package com.vetsync.app.uniremington.controller;

import com.vetsync.app.uniremington.entity.ConfiguracionParametro;
import com.vetsync.app.uniremington.repository.ConfiguracionParametroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/uni/config")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ConfiguracionController {

    private final ConfiguracionParametroRepository repository;

    @GetMapping("/{tipo}")
    public List<ConfiguracionParametro> getByTipo(@PathVariable String tipo) {
        return repository.findByTipo(tipo);
    }

    @PostMapping
    public ConfiguracionParametro create(@RequestBody ConfiguracionParametro param) {
        return repository.save(param);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConfiguracionParametro> update(@PathVariable Long id, @RequestBody ConfiguracionParametro details) {
        return repository.findById(id).map(p -> {
            p.setValor(details.getValor());
            p.setDescripcion(details.getDescripcion());
            p.setActivo(details.isActivo());
            return ResponseEntity.ok(repository.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
