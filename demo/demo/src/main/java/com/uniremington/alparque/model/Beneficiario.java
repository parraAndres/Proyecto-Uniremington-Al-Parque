package com.uniremington.alparque.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.uniremington.alparque.model.enums.Genero;
import com.uniremington.alparque.model.enums.TipoPoblacion;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

@Entity
@Table(name="beneficiarios")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Beneficiario {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message="El nombre no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El número de documento es obligatorio")
    @Pattern(regexp = "\\d+", message = "El documento debe contener solo números")
    @Column(name="numero_documento", unique = true, nullable=false, length = 20)
    private String numeroDocumento;

    @Min(value = 0, message="La edad debe ser positiva")
    @Max(value = 120, message="Edad no válida")
    private Integer edad;

    @NotNull(message="El género es obligatorio")
    @Enumerated(EnumType.STRING)
    private Genero genero;

    @Pattern(regexp = "\\+?\\d{7,15}", message = "Teléfono inválido")
    @Column(length = 15)
    private String telefono;

    @NotBlank(message = "El municipio es obligatorio")
    @Column(nullable = false, length = 50)
    private String municipio;

    @Column(name = "barrioVereda", length = 50)
    private String barrioVereda;

    @NotNull(message ="El tipo de población es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(name ="tipo_poblacion")
    private TipoPoblacion tipoPoblacion;

    @NotBlank(message = "El servicio solicitado es obligatorio")
    @Column(name="servicio_solicitado", nullable = false, length=100)
    private String servicioSolicitado;

    @NotNull(message = "Debe indicar si autoriza el uso de datos")
    @Column(name = "autoriza_datos", nullable = false)
    private Boolean autorizaDatos;//Se utiliza true o false para autorizar

    @CreationTimestamp
    @Column(name="fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;//esta fecha es automática


    @OneToMany(mappedBy = "beneficiario", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Servicio> servicios = new ArrayList<>();
}
