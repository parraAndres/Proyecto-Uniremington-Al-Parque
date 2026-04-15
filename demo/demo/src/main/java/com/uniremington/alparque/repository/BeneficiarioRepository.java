package com.uniremington.alparque.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.uniremington.alparque.model.Beneficiario;

@Repository
public interface BeneficiarioRepository extends JpaRepository<Beneficiario, UUID> {

	boolean existsByNumeroDocumento(String numeroDocumento);

	boolean existsByNumeroDocumentoAndIdNot(String numeroDocumento, UUID id);

	Optional<Beneficiario> findByNumeroDocumento(String numeroDocumento);

	long countByFechaRegistroBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

	@Query("select b.municipio, count(b) from Beneficiario b group by b.municipio")
	List<Object[]> countBeneficiariosByMunicipio();

	@Query("select b.municipio, count(b) from Beneficiario b where b.fechaRegistro between :fechaInicio and :fechaFin group by b.municipio")
	List<Object[]> countBeneficiariosByMunicipioBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
			@Param("fechaFin") LocalDateTime fechaFin);

	@Query("select year(b.fechaRegistro), month(b.fechaRegistro), count(b) "
			+ "from Beneficiario b "
			+ "where b.fechaRegistro between :fechaInicio and :fechaFin "
			+ "group by year(b.fechaRegistro), month(b.fechaRegistro) "
			+ "order by year(b.fechaRegistro), month(b.fechaRegistro)")
	List<Object[]> countBeneficiariosByMesBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
			@Param("fechaFin") LocalDateTime fechaFin);
}
