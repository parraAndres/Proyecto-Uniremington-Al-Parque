package com.uniremington.alparque.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Respuesta paginada de lotes de sincronización.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SincronizacionLotePageResponseDTO {

    private List<SincronizacionLoteResumenDTO> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
