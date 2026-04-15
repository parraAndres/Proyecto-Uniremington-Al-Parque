package com.uniremington.alparque.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SincronizacionItemResultadoDTO {

    private String tipoEntidad;
    private String idempotencyKey;
    private String clientRecordId;
    private String estado;
    private String serverId;
    private String mensaje;
}