package com.uniremington.alparque.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.uniremington.alparque.dto.request.BeneficiarioRequestDTO;
import com.uniremington.alparque.dto.response.BeneficiarioResponseDTO;
import com.uniremington.alparque.model.Beneficiario;

@Mapper(componentModel = "spring")
public interface BeneficiarioMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "fechaRegistro", ignore = true)
	@Mapping(target = "servicios", ignore = true)
	Beneficiario toEntity(BeneficiarioRequestDTO request);

	BeneficiarioResponseDTO toResponseDTO(Beneficiario beneficiario);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "fechaRegistro", ignore = true)
	@Mapping(target = "servicios", ignore = true)
	void updateEntityFromRequest(BeneficiarioRequestDTO request, @MappingTarget Beneficiario beneficiario);
}
