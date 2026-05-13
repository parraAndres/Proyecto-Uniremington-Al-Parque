package com.vetsync.app.uniremington.controller;

import com.vetsync.app.uniremington.entity.Jornada;
import com.vetsync.app.uniremington.entity.ServicioSocial;
import com.vetsync.app.uniremington.entity.UsuarioUniremington;
import com.vetsync.app.uniremington.service.DocenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/uni/docente")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DocenteController {

    private final DocenteService docenteService;

    @GetMapping("/{id}/jornadas")
    public ResponseEntity<List<Jornada>> getJornadas(@PathVariable String id) {
        return ResponseEntity.ok(docenteService.getMisJornadas(id));
    }

    @GetMapping("/{id}/estudiantes")
    public ResponseEntity<List<UsuarioUniremington>> getEstudiantes(@PathVariable String id) {
        return ResponseEntity.ok(docenteService.getMisEstudiantes(id));
    }

    @GetMapping("/{id}/casos-pendientes")
    public ResponseEntity<List<ServicioSocial>> getCasos(@PathVariable String id) {
        return ResponseEntity.ok(docenteService.getCasosPendientes(id));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> getStats(@PathVariable String id) {
        return ResponseEntity.ok(docenteService.getMisEstadisticas(id));
    }
}
