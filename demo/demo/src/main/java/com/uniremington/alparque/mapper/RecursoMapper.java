package com.uniremington.alparque.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.uniremington.alparque.dto.request.RecursoRequestDTO;
import com.uniremington.alparque.dto.response.RecursoResponseDTO;
import com.uniremington.alparque.model.Recurso;

@Mapper(componentModel = "spring")
public interface RecursoMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "fechaRegistro", ignore = true)
	Recurso toEntity(RecursoRequestDTO request);

	RecursoResponseDTO toResponse(Recurso recurso);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "fechaRegistro", ignore = true)
	void updateEntity(RecursoRequestDTO request, @MappingTarget Recurso recurso);
}
