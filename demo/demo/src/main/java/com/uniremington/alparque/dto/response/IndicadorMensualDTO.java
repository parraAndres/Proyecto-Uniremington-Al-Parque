package com.uniremington.alparque.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IndicadorMensualDTO {

    private String mes;
    private long beneficiarios;
    private long servicios;
}