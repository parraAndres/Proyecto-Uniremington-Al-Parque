package com.uniremington.alparque.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "estudiantes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Estudiante {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@NotBlank(message = "El nombre completo es obligatorio")
	@Column(nullable = false, length = 120)
	private String nombreCompleto;

	@NotBlank(message = "El documento es obligatorio")
	@Column(nullable = false, unique = true, length = 20)
	private String numeroDocumento;

	@NotBlank(message = "El programa academico es obligatorio")
	@Column(nullable = false, length = 120)
	private String programaAcademico;

	@NotNull(message = "Las horas de participacion son obligatorias")
	@Column(nullable = false)
	private Integer horasParticipacion;

	@CreationTimestamp
	@Column(name = "fecha_registro", updatable = false)
	private LocalDateTime fechaRegistro;
}
