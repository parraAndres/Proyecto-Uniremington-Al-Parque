package com.vetsync.app.uniremington.controller;

import com.vetsync.app.uniremington.dto.DomainRequests;
import com.vetsync.app.uniremington.entity.*;
import com.vetsync.app.uniremington.service.DomainService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/social")
@RequiredArgsConstructor
public class SocialDomainController {

    private final DomainService domainService;

    @PostMapping("/beneficiarios")
    public ResponseEntity<Beneficiario> createBeneficiario(@Valid @RequestBody DomainRequests.BeneficiarioRequest request) {
        return ResponseEntity.ok(domainService.upsertBeneficiario(request));
    }

    @PostMapping("/servicios")
    public ResponseEntity<ServicioSocial> createServicio(@Valid @RequestBody DomainRequests.ServicioRequest request) {
        return ResponseEntity.ok(domainService.createServicio(request));
    }

    @PostMapping("/seguimientos")
    public ResponseEntity<SeguimientoCaso> createSeguimiento(@Valid @RequestBody DomainRequests.SeguimientoRequest request) {
        return ResponseEntity.ok(domainService.createSeguimiento(request));
    }

    @PostMapping("/academico/participaciones")
    public ResponseEntity<ParticipacionAcademica> createAcademico(@Valid @RequestBody DomainRequests.AcademicoRequest request) {
        return ResponseEntity.ok(domainService.createAcademico(request));
    }

    @PostMapping("/recursos")
    public ResponseEntity<RecursoAporte> createRecurso(@Valid @RequestBody DomainRequests.RecursoRequest request) {
        return ResponseEntity.ok(domainService.createRecurso(request));
    }
}
