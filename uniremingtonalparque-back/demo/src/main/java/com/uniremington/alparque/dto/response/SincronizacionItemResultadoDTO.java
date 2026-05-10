package com.uniremington.alparque.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resultado individual de un ítem procesado en el lote de sincronización.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SincronizacionItemResultadoDTO {

    /** Tipo de entidad procesada (ej: "BENEFICIARIO") */
    private String tipoEntidad;

    /** Clave de idempotencia del ítem */
    private String idempotencyKey;

    /** ID local del registro en el cliente */
    private String clientRecordId;

    /** Estado resultante: CREATED, UPDATED, DUPLICATE, CONFLICT, ERROR */
    private String estado;

    /** ID del registro en el servidor (null si hubo error) */
    private String serverId;

    /** Mensaje descriptivo del resultado */
    private String mensaje;
}
