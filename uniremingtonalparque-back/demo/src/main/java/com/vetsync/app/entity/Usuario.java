package com.vetsync.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String email;

    @Column(nullable = false, unique = true, length = 30)
    private String documento;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 100)
    private String facultad;

    @Column(length = 120)
    private String programa;

    @Column(length = 50)
    private String genero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    public enum Rol {
        ADMIN, VETERINARIO, FARMACEUTICO, AUXILIAR, ESTUDIANTE, PROFESOR, CLIENTE
    }
}
