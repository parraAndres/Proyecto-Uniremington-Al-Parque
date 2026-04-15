package com.uniremington.alparque.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.uniremington.alparque.model.enums.Clasificacion;
import com.uniremington.alparque.model.enums.Prioridad;

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
@Table(name = "diagnosticos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Diagnostico {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@NotBlank(message = "El municipio es obligatorio")
	@Column(nullable = false, length = 80)
	private String municipio;

	@NotBlank(message = "La problematica es obligatoria")
	@Column(nullable = false, length = 600)
	private String problematica;

	@NotNull(message = "La clasificacion es obligatoria")
	@Enumerated(EnumType.STRING)
	private Clasificacion clasificacion;

	@NotNull(message = "La prioridad es obligatoria")
	@Enumerated(EnumType.STRING)
	private Prioridad prioridad;

	@CreationTimestamp
	@Column(name = "fecha_registro", updatable = false)
	private LocalDateTime fechaRegistro;
}
