package com.uniremington.alparque.dto.request;

import java.util.UUID;

import com.uniremington.alparque.model.enums.Facultad;
import com.uniremington.alparque.model.enums.ResultadoAtencion;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServicioRequestDTO {

	@NotNull(message = "El beneficiario es obligatorio")
	private UUID beneficiarioId;

	@NotBlank(message = "El tipo de servicio es obligatorio")
	@Size(max = 100, message = "El tipo de servicio no puede exceder 100 caracteres")
	private String tipoServicio;

	@NotNull(message = "La facultad es obligatoria")
	private Facultad facultad;

	@Size(max = 500, message = "La descripcion de la atencion no puede exceder 500 caracteres")
	private String descripcionAtencion;

	@Min(value = 1, message = "El tiempo debe ser positivo")
	private Integer tiempoAtencionMinutos;

	@NotNull(message = "El resultado es obligatorio")
	private ResultadoAtencion resultado;

	@Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
	private String observaciones;

	@Size(max = 255, message = "La evidencia no puede exceder 255 caracteres")
	private String evidencias;
}
