package com.vetsync.app.uniremington.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "uni_configuracion_parametros", indexes = {
    @Index(name = "idx_param_tipo", columnList = "tipo")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiguracionParametro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String tipo; // FACULTAD, TIPO_SERVICIO, CATEGORIA, PROBLEMATICA, ESTADO_CASO

    @Column(nullable = false, length = 100)
    private String valor;

    @Column(length = 255)
    private String descripcion;

    @Builder.Default
    private boolean activo = true;
}
