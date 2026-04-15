package com.uniremington.alparque.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.uniremington.alparque.model.enums.Facultad;
import com.uniremington.alparque.model.enums.ResultadoAtencion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServicioResponseDTO {

	private UUID id;
	private UUID beneficiarioId;
	private String tipoServicio;
	private Facultad facultad;
	private String descripcionAtencion;
	private Integer tiempoAtencionMinutos;
	private ResultadoAtencion resultado;
	private String observaciones;
	private String evidencias;
	private LocalDateTime fechaAtencion;
}
