package com.uniremington.alparque.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Future;
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
public class SeguimientoRequestDTO {

	@NotNull(message = "El caso es obligatorio")
	private UUID casoId;

	@NotBlank(message = "El registro de avances es obligatorio")
	private String registroAvances;

	@Future(message = "La fecha de seguimiento debe ser futura")
	private LocalDateTime fechaSeguimientoProgramado;
}
