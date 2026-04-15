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
	private List<SincronizacionItemResultadoDTO> resultados = new ArrayList<>();
}
