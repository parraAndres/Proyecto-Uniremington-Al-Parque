package com.uniremington.alparque.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.uniremington.alparque.model.enums.TipoAporte;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecursoResponseDTO {

    private UUID id;
    private String fuenteAporte;
    private TipoAporte tipoAporte;
    private BigDecimal valor;
    private String descripcion;
    private LocalDateTime fechaRegistro;
}