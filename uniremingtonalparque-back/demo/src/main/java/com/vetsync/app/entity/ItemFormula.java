package com.vetsync.app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items_formula")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ItemFormula {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formula_id", nullable = false)
    private FormulaMedica formula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(length = 200)
    private String instrucciones;
}
