package com.uniremington.alparque.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.uniremington.alparque.dto.request.EstudianteRequestDTO;
import com.uniremington.alparque.dto.response.EstudianteResponseDTO;
import com.uniremington.alparque.model.Estudiante;

@Mapper(componentModel = "spring")
public interface EstudianteMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "fechaRegistro", ignore = true)
	Estudiante toEntity(EstudianteRequestDTO request);

	EstudianteResponseDTO toResponse(Estudiante estudiante);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "fechaRegistro", ignore = true)
	void updateEntity(EstudianteRequestDTO request, @MappingTarget Estudiante estudiante);
}
