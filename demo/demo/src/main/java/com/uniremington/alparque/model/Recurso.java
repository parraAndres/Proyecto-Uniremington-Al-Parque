package com.uniremington.alparque.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.uniremington.alparque.model.enums.TipoAporte;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "recursos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recurso {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@NotBlank(message = "La fuente de aporte es obligatoria")
	@Column(nullable = false, length = 120)
	private String fuenteAporte;

	@NotNull(message = "El tipo de aporte es obligatorio")
	@Enumerated(EnumType.STRING)
	private TipoAporte tipoAporte;

	@Column(precision = 14, scale = 2)
	private BigDecimal valor;

	@Column(length = 300)
	private String descripcion;

	@CreationTimestamp
	@Column(name = "fecha_registro", updatable = false)
	private LocalDateTime fechaRegistro;
}
