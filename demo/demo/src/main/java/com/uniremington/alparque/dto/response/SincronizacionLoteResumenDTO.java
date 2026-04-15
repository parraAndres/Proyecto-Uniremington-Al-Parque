package com.uniremington.alparque.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SincronizacionLoteResumenDTO {

    private String loteId;
    private String dispositivoId;
    private String estadoLote;
    private int totalRecibidos;
    private int procesados;
    private int duplicados;
    private int conflictos;
    private int errores;
    private LocalDateTime fechaActualizacion;
}