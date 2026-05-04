package com.vetsync.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_pagos")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class HistorialPago {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", nullable = false)
    private Factura factura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Usuario administrador;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago;

    private LocalDateTime fechaPago = LocalDateTime.now();

    @Column(length = 200)
    private String observaciones;

    public enum MetodoPago { EFECTIVO, TRANSFERENCIA, TARJETA_CREDITO, TARJETA_DEBITO }
}
