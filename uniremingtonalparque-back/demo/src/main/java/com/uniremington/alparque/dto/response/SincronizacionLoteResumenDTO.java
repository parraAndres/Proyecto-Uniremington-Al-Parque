package com.uniremington.alparque.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Resumen de un lote de sincronización para listados paginados.
 */
@Data
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
    private LocalDateTime fechaLote;
}
