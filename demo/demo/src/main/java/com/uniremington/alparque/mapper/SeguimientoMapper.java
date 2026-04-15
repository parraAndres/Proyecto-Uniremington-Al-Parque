package com.uniremington.alparque.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.uniremington.alparque.dto.request.SeguimientoRequestDTO;
import com.uniremington.alparque.dto.response.SeguimientoResponseDTO;
import com.uniremington.alparque.model.Seguimiento;

@Mapper(componentModel = "spring")
public interface SeguimientoMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "caso", ignore = true)
	@Mapping(target = "fechaRegistro", ignore = true)
	Seguimiento toEntity(SeguimientoRequestDTO request);

	@Mapping(target = "casoId", source = "caso.id")
	SeguimientoResponseDTO toResponse(Seguimiento seguimiento);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "caso", ignore = true)
	@Mapping(target = "fechaRegistro", ignore = true)
	void updateEntity(SeguimientoRequestDTO request, @MappingTarget Seguimiento seguimiento);
}
