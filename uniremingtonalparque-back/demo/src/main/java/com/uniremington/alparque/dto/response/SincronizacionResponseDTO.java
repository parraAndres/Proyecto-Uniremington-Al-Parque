package com.uniremington.alparque.dto.response;

import lombok.Data;

import java.util.List;

/**
 * Respuesta completa al procesar un lote de sincronización.
 */
@Data
public class SincronizacionResponseDTO {

    private String loteId;
    private String dispositivoId;
    private String estadoLote;
    private String mensaje;

    private int totalRecibidos;
    private int procesados;
    private int duplicados;
    private int conflictos;
    private int errores;

    private List<SincronizacionItemResultadoDTO> resultados;
}
