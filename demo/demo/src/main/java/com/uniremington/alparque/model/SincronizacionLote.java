package com.uniremington.alparque.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sincronizacion_lotes", uniqueConstraints = {
    @UniqueConstraint(name = "uk_lote_dispositivo", columnNames = { "lote_id", "dispositivo_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SincronizacionLote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "lote_id", nullable = false, length = 120)
    private String loteId;

    @Column(name = "dispositivo_id", nullable = false, length = 120)
    private String dispositivoId;

    @Column(name = "estado_lote", nullable = false, length = 30)
    private String estadoLote;

    @Column(name = "total_recibidos", nullable = false)
    private int totalRecibidos;

    @Column(nullable = false)
    private int procesados;

    @Column(nullable = false)
    private int duplicados;

    @Column(nullable = false)
    private int conflictos;

    @Column(nullable = false)
    private int errores;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}