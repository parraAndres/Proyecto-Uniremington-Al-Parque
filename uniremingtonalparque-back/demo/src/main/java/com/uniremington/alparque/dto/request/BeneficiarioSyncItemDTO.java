package com.uniremington.alparque.dto.request;

import com.uniremington.alparque.model.enums.Genero;
import com.uniremington.alparque.model.enums.TipoPoblacion;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO que representa un beneficiario individual dentro de un lote de sincronización.
 */
@Data
public class BeneficiarioSyncItemDTO {

    /** Clave de idempotencia: identifica unívocamente este evento de sincronización */
    private String idempotencyKey;

    /** ID local del registro en el cliente (dispositivo offline) */
    private String clientRecordId;

    /** Timestamp del cliente cuando se creó/modificó este registro */
    private LocalDateTime clientUpdatedAt;

    private String nombre;
    private String numeroDocumento;
    private Integer edad;
    private Genero genero;
    private String telefono;
    private String municipio;
    private String barrioVereda;
    private TipoPoblacion tipoPoblacion;
    private String servicioSolicitado;
    private Boolean autorizaDatos;
}
