package com.uniremington.alparque.dto.request;

import com.uniremington.alparque.model.enums.Clasificacion;
import com.uniremington.alparque.model.enums.Prioridad;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosticoRequestDTO {

	@NotBlank(message = "El municipio es obligatorio")
	private String municipio;

	@NotBlank(message = "La problematica es obligatoria")
	private String problematica;

	@NotNull(message = "La clasificacion es obligatoria")
	private Clasificacion clasificacion;

	@NotNull(message = "La prioridad es obligatoria")
	private Prioridad prioridad;
}
