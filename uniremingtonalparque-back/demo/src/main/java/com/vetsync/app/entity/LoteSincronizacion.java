package com.vetsync.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lotes_sincronizacion",
       uniqueConstraints = @UniqueConstraint(columnNames = "clave_idempotencia"))
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LoteSincronizacion {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "clave_idempotencia", nullable = false, length = 36)
    private String claveIdempotencia;

    @Column(nullable = false)
    private String origen;

    @Column(nullable = false)
    private Integer totalRegistros;

    @Column(nullable = false)
    private Integer procesados;

    @Column(nullable = false)
    private Integer duplicados;

    @Column(nullable = false)
    private Integer errores;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoLote estado;

    private LocalDateTime fechaProcesado = LocalDateTime.now();
    private LocalDateTime lastModified;

    @Column(columnDefinition = "TEXT")
    private String detalleError;

    public enum EstadoLote { EXITOSO, DUPLICADO, ERROR, PARCIAL }
}
