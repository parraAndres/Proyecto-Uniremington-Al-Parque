package com.uniremington.alparque.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uniremington.alparque.model.Servicio;

public interface ServicioRepository extends JpaRepository<Servicio, UUID> {

	List<Servicio> findByBeneficiarioId(UUID beneficiarioId);

	long countByFechaAtencionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

	@Query("select s.facultad, count(s) from Servicio s group by s.facultad")
	List<Object[]> countServiciosByFacultad();

	@Query("select s.resultado, count(s) from Servicio s group by s.resultado")
	List<Object[]> countServiciosByResultado();

	@Query("select s.facultad, count(s) from Servicio s where s.fechaAtencion between :fechaInicio and :fechaFin group by s.facultad")
	List<Object[]> countServiciosByFacultadBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
			@Param("fechaFin") LocalDateTime fechaFin);

	@Query("select s.resultado, count(s) from Servicio s where s.fechaAtencion between :fechaInicio and :fechaFin group by s.resultado")
	List<Object[]> countServiciosByResultadoBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
			@Param("fechaFin") LocalDateTime fechaFin);

	@Query("select year(s.fechaAtencion), month(s.fechaAtencion), count(s) "
			+ "from Servicio s "
			+ "where s.fechaAtencion between :fechaInicio and :fechaFin "
			+ "group by year(s.fechaAtencion), month(s.fechaAtencion) "
			+ "order by year(s.fechaAtencion), month(s.fechaAtencion)")
	List<Object[]> countServiciosByMesBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
			@Param("fechaFin") LocalDateTime fechaFin);

	@Query("select year(s.fechaAtencion), month(s.fechaAtencion), s.facultad, count(s) "
			+ "from Servicio s "
			+ "where s.fechaAtencion between :fechaInicio and :fechaFin "
			+ "group by year(s.fechaAtencion), month(s.fechaAtencion), s.facultad "
			+ "order by year(s.fechaAtencion), month(s.fechaAtencion), s.facultad")
	List<Object[]> countServiciosByMesYFacultadBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
			@Param("fechaFin") LocalDateTime fechaFin);
}
