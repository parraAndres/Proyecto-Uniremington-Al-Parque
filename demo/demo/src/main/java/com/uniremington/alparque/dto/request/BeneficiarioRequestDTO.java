package com.uniremington.alparque.dto.request;

import com.uniremington.alparque.model.enums.Genero;
import com.uniremington.alparque.model.enums.TipoPoblacion;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiarioRequestDTO {

	@NotBlank(message = "El nombre es obligatorio")
	@Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
	private String nombre;

	@NotBlank(message = "El numero de documento es obligatorio")
	@Pattern(regexp = "\\d+", message = "El documento debe contener solo numeros")
	private String numeroDocumento;

	@Min(value = 0, message = "La edad debe ser positiva")
	@Max(value = 120, message = "Edad no valida")
	private Integer edad;

	@NotNull(message = "El genero es obligatorio")
	private Genero genero;

	@Pattern(regexp = "\\+?\\d{7,15}", message = "Telefono invalido")
	private String telefono;

	@NotBlank(message = "El municipio es obligatorio")
	@Size(max = 50, message = "El municipio no puede exceder 50 caracteres")
	private String municipio;

	@Size(max = 50, message = "El barrio o vereda no puede exceder 50 caracteres")
	private String barrioVereda;

	@NotNull(message = "El tipo de poblacion es obligatorio")
	private TipoPoblacion tipoPoblacion;

	@NotBlank(message = "El servicio solicitado es obligatorio")
	@Size(max = 100, message = "El servicio solicitado no puede exceder 100 caracteres")
	private String servicioSolicitado;

	@NotNull(message = "Debe indicar si autoriza el uso de datos")
	private Boolean autorizaDatos;
}
