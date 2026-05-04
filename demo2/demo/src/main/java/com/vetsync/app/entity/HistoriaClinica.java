package com.vetsync.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "historias_clinicas")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class HistoriaClinica {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cita_id", nullable = false, unique = true)
    private Cita cita;

    @Column(columnDefinition = "TEXT")
    private String anamnesis;

    @Column(columnDefinition = "TEXT")
    private String diagnostico;

    @Column(columnDefinition = "TEXT")
    private String tratamiento;

    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @OneToMany(mappedBy = "historiaClinica", cascade = CascadeType.ALL)
    private List<FormulaMedica> formulas;
}
