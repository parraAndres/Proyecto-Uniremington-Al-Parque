package com.uniremington.alparque.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Registro de idempotencia: cada evento de sincronización queda registrado
 * con su clave idempotente para evitar reprocesamiento en reenvíos.
 */
@Entity
@Table(name = "alparque_sincronizacion_eventos", indexes = {
        @Index(name = "idx_alp_ev_idempotency", columnList = "idempotency_key", unique = true),
        @Index(name = "idx_alp_ev_tipo_entidad", columnList = "tipo_entidad, entidad_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SincronizacionEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idempotency_key", nullable = false, length = 200, unique = true)
    private String idempotencyKey;

    @Column(name = "tipo_entidad", nullable = false, length = 50)
    private String tipoEntidad;

    @Column(name = "entidad_id")
    private UUID entidadId;

    @Column(name = "client_record_id", length = 200)
    private String clientRecordId;

    /** Timestamp en el cliente cuando se creó/modificó el registro */
    @Column(name = "fecha_cliente")
    private LocalDateTime fechaCliente;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    public void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
}
