package com.uniremington.alparque.dto.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO raíz del lote de sincronización.
 * El cliente envía un único lote con todos los registros pendientes
 * agrupados por tipo de entidad.
 */
@Data
public class SincronizacionBatchRequestDTO {

    /** Identificador único del lote generado en el dispositivo */
    private String loteId;

    /** Identificador del dispositivo que envía el lote */
    private String dispositivoId;

    /** Lista de beneficiarios a sincronizar en este lote */
    private List<BeneficiarioSyncItemDTO> beneficiarios = new ArrayList<>();
}
