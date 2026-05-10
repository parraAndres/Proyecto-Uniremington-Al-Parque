package com.uniremington.alparque.model;

import com.uniremington.alparque.model.enums.EstadoLote;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Representa un lote de sincronización enviado desde un dispositivo.
 * Cada lote tiene un identificador único (loteId) por dispositivo.
 */
@Entity
@Table(name = "alparque_sincronizacion_lotes", indexes = {
        @Index(name = "idx_alp_lote_id_disp", columnList = "lote_id, dispositivo_id", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SincronizacionLote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lote_id", nullable = false, length = 100)
    private String loteId;

    @Column(name = "dispositivo_id", nullable = false, length = 100)
    private String dispositivoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_lote", nullable = false, length = 20)
    private EstadoLote estadoLote;

    @Column(name = "total_recibidos")
    private Integer totalRecibidos;

    @Column(name = "procesados")
    private Integer procesados;

    @Column(name = "duplicados")
    private Integer duplicados;

    @Column(name = "conflictos")
    private Integer conflictos;

    @Column(name = "errores")
    private Integer errores;

    @Column(name = "fecha_lote")
    private LocalDateTime fechaLote;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    public void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
}
