package com.vetsync.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "mascotas")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Mascota {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String especie;

    @Column(length = 50)
    private String raza;

    @Min(value = 1, message = "La edad debe ser mayor a 0")
    @Column(nullable = false)
    private Integer edad;

    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    private LocalDate fechaRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cita> citas;

    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PlanSanitario> planesSanitarios;

    public enum Sexo { MACHO, HEMBRA }
}

