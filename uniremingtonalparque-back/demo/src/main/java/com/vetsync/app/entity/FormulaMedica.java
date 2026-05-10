package com.vetsync.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "formulas_medicas")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class FormulaMedica {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "historia_clinica_id", nullable = false)
    private HistoriaClinica historiaClinica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Usuario veterinario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmaceutico_id")
    private Usuario farmaceutico;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String medicamentos;

    @Enumerated(EnumType.STRING)
    private EstadoFormula estado = EstadoFormula.PENDIENTE;

    private LocalDateTime fechaEmision = LocalDateTime.now();
    private LocalDateTime fechaDispensacion;

    @OneToMany(mappedBy = "formula", cascade = CascadeType.ALL)
    private List<ItemFormula> items;

    public enum EstadoFormula { PENDIENTE, VALIDADA, DISPENSADA, RECHAZADA }
}
