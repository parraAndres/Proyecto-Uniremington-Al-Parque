package com.vetsync.app.service.impl;

import com.vetsync.app.dto.request.ProductoRequest;
import com.vetsync.app.entity.Producto;
import com.vetsync.app.exception.RecursoNotFoundException;
import com.vetsync.app.exception.ReglaDeNegocioException;
import com.vetsync.app.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductoService {

    private final ProductoRepository productoRepository;

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public Producto findById(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNotFoundException("Producto no encontrado con id: " + id));
    }

    public List<Producto> findStockBajo() {
        return productoRepository.findByStockActualLessThanEqualStockMinimo();
    }

    @Transactional
    public Producto create(ProductoRequest request) {
        if (productoRepository.existsByCodigo(request.getCodigo())) {
            throw new ReglaDeNegocioException("Ya existe un producto con el código: " + request.getCodigo());
        }

        Producto producto = Producto.builder()
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .stockActual(request.getStockActual())
                .stockMinimo(request.getStockMinimo())
                .precio(request.getPrecio())
                .unidadMedida(request.getUnidadMedida())
                .activo(true)
                .build();

        return productoRepository.save(producto);
    }

    @Transactional
    public Producto update(Long id, ProductoRequest request) {
        Producto producto = findById(id);

        if (!producto.getCodigo().equals(request.getCodigo()) &&
            productoRepository.existsByCodigo(request.getCodigo())) {
            throw new ReglaDeNegocioException("Ya existe un producto con el código: " + request.getCodigo());
        }

        producto.setCodigo(request.getCodigo());
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setStockActual(request.getStockActual());
        producto.setStockMinimo(request.getStockMinimo());
        producto.setPrecio(request.getPrecio());
        producto.setUnidadMedida(request.getUnidadMedida());

        return productoRepository.save(producto);
    }

    @Transactional
    public Producto ajustarStock(Long id, int cantidad) {
        Producto producto = findById(id);
        int nuevoStock = producto.getStockActual() + cantidad;
        if (nuevoStock < 0) {
            throw new ReglaDeNegocioException("Stock insuficiente. Stock actual: " + producto.getStockActual());
        }
        producto.setStockActual(nuevoStock);
        return productoRepository.save(producto);
    }

    @Transactional
    public void delete(Long id) {
        Producto producto = findById(id);
        producto.setActivo(false);
        productoRepository.save(producto);
    }
}
