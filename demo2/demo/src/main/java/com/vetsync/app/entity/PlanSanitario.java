package com.vetsync.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "planes_sanitarios")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PlanSanitario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    @Column(nullable = false, length = 100)
    private String vacuna;

    @Column(nullable = false)
    private LocalDate fechaAplicacion;

    private LocalDate proximaAplicacion;

    @Enumerated(EnumType.STRING)
    private EstadoVacuna estado = EstadoVacuna.VIGENTE;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    public enum EstadoVacuna { VIGENTE, VENCIDA, PENDIENTE }
}
