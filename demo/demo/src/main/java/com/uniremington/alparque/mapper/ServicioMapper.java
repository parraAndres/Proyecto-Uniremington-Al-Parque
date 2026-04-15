package com.uniremington.alparque.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.uniremington.alparque.dto.request.ServicioRequestDTO;
import com.uniremington.alparque.dto.response.ServicioResponseDTO;
import com.uniremington.alparque.model.Servicio;

@Mapper(componentModel = "spring")
public interface ServicioMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "beneficiario", ignore = true)
	@Mapping(target = "fechaAtencion", ignore = true)
	Servicio toEntity(ServicioRequestDTO request);

	@Mapping(target = "beneficiarioId", source = "beneficiario.id")
	ServicioResponseDTO toResponseDTO(Servicio servicio);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "beneficiario", ignore = true)
	@Mapping(target = "fechaAtencion", ignore = true)
	void updateEntityFromRequest(ServicioRequestDTO request, @MappingTarget Servicio servicio);
}
