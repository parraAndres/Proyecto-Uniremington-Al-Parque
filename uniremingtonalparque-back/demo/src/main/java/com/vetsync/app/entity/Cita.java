package com.vetsync.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "citas")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Cita {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Usuario veterinario;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(length = 200)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCita estado = EstadoCita.PROGRAMADA;

    @OneToOne(mappedBy = "cita", cascade = CascadeType.ALL)
    private HistoriaClinica historiaClinica;

    public enum EstadoCita { PROGRAMADA, EN_CURSO, COMPLETADA, CANCELADA }
}
