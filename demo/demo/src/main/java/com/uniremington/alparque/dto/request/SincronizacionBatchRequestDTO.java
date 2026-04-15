package com.uniremington.alparque.dto.request;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SincronizacionBatchRequestDTO {

    @NotBlank(message = "El id del dispositivo es obligatorio")
    private String dispositivoId;

    @NotBlank(message = "El id del lote es obligatorio")
    private String loteId;

    @Valid
    private List<BeneficiarioSyncItemDTO> beneficiarios = new ArrayList<>();

    @Valid
    private List<ServicioSyncItemDTO> servicios = new ArrayList<>();
}