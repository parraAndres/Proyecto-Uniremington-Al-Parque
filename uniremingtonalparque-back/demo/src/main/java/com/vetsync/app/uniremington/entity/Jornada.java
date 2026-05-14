package com.vetsync.app.uniremington.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "uni_jornadas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Jornada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private String municipio;

    private String vereda;
    private String barrio;

    @Column(columnDefinition = "LONGTEXT")
    private String descripcion;

    @Column(columnDefinition = "LONGTEXT")
    private String imagenUrl;

    @Builder.Default
    private String estado = "PROGRAMADA"; // PROGRAMADA, EN_CURSO, FINALIZADA, CANCELADA

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "uni_jornada_personal",
        joinColumns = @JoinColumn(name = "jornada_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<UsuarioUniremington> personalAsignado = new ArrayList<>();

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
