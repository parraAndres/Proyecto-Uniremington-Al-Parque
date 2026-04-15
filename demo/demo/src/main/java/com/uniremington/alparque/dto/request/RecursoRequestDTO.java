package com.uniremington.alparque.dto.request;

import java.math.BigDecimal;

import com.uniremington.alparque.model.enums.TipoAporte;

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
public class RecursoRequestDTO {

	@NotBlank(message = "La fuente de aporte es obligatoria")
	private String fuenteAporte;

	@NotNull(message = "El tipo de aporte es obligatorio")
	private TipoAporte tipoAporte;

	private BigDecimal valor;

	private String descripcion;
}
