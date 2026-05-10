package com.vetsync.app.controller;

import com.vetsync.app.dto.request.ProductoRequest;
import com.vetsync.app.entity.Producto;
import com.vetsync.app.service.impl.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/inventario")
@RequiredArgsConstructor
@Tag(name = "Inventario", description = "Gestión de productos e inventario farmacéutico")
@SecurityRequirement(name = "bearerAuth")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    @Operation(summary = "Listar todos los productos")
    public ResponseEntity<List<Producto>> findAll() {
        return ResponseEntity.ok(productoService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    public ResponseEntity<Producto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.findById(id));
    }

    @GetMapping("/stock-bajo")
    @Operation(summary = "Listar productos con stock bajo")
    public ResponseEntity<List<Producto>> findStockBajo() {
        return ResponseEntity.ok(productoService.findStockBajo());
    }

    @PostMapping
    @Operation(summary = "Registrar nuevo producto")
    public ResponseEntity<Producto> create(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto")
    public ResponseEntity<Producto> update(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(productoService.update(id, request));
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Ajustar stock del producto (positivo = entrada, negativo = salida)")
    public ResponseEntity<Producto> ajustarStock(@PathVariable Long id,
                                                  @RequestParam int cantidad) {
        return ResponseEntity.ok(productoService.ajustarStock(id, cantidad));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar producto (soft delete)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
