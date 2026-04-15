package com.uniremington.alparque.dto.request;

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
public class EstudianteRequestDTO {

	@NotBlank(message = "El nombre completo es obligatorio")
	private String nombreCompleto;

	@NotBlank(message = "El documento es obligatorio")
	private String numeroDocumento;

	@NotBlank(message = "El programa academico es obligatorio")
	private String programaAcademico;

	@NotNull(message = "Las horas de participacion son obligatorias")
	private Integer horasParticipacion;
}
