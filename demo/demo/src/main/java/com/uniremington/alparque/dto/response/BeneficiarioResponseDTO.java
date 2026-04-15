package com.uniremington.alparque.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.uniremington.alparque.model.enums.Genero;
import com.uniremington.alparque.model.enums.TipoPoblacion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiarioResponseDTO {

	private UUID id;
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
	private LocalDateTime fechaRegistro;
}
