package com.uniremington.alparque.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.uniremington.alparque.dto.request.DiagnosticoRequestDTO;
import com.uniremington.alparque.dto.response.DiagnosticoResponseDTO;
import com.uniremington.alparque.model.Diagnostico;

@Mapper(componentModel = "spring")
public interface DiagnosticoMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "fechaRegistro", ignore = true)
	Diagnostico toEntity(DiagnosticoRequestDTO request);

	DiagnosticoResponseDTO toResponse(Diagnostico diagnostico);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "fechaRegistro", ignore = true)
	void updateEntity(DiagnosticoRequestDTO request, @MappingTarget Diagnostico diagnostico);
}
