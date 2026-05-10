package com.uniremington.alparque.model;

import com.uniremington.alparque.model.enums.EstadoItem;
import jakarta.persistence.*;
import lombok.*;

/**
 * Resultado detallado por cada ítem procesado dentro de un lote.
 */
@Entity
@Table(name = "alparque_sincronizacion_resultado_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SincronizacionResultadoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id_fk", nullable = false)
    private SincronizacionLote lote;

    @Column(name = "tipo_entidad", nullable = false, length = 50)
    private String tipoEntidad;

    @Column(name = "idempotency_key", nullable = false, length = 200)
    private String idempotencyKey;

    @Column(name = "client_record_id", length = 200)
    private String clientRecordId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoItem estado;

    @Column(name = "server_id", length = 200)
    private String serverId;

    @Column(name = "mensaje", length = 500)
    private String mensaje;
}
