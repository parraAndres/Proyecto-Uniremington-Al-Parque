package com.uniremington.alparque.dto.response;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SincronizacionLotePageResponseDTO {

    private List<SincronizacionLoteResumenDTO> content = new ArrayList<>();
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;
}