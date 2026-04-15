package com.uniremington.alparque.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sincronizacion_eventos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SincronizacionEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 120)
    private String idempotencyKey;

    @Column(name = "lote_id", nullable = false, length = 120)
    private String loteId;

    @Column(name = "dispositivo_id", nullable = false, length = 120)
    private String dispositivoId;

    @Column(name = "client_record_id", length = 120)
    private String clientRecordId;

    @Column(name = "fecha_cliente")
    private LocalDateTime fechaCliente;

    @Column(name = "tipo_entidad", nullable = false, length = 30)
    private String tipoEntidad;

    @Column(name = "entidad_id", nullable = false)
    private UUID entidadId;

    @Column(nullable = false, length = 30)
    private String estado;

    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;
}