package com.vetsync.app.mapper;

import com.vetsync.app.dto.request.ClienteRequest;
import com.vetsync.app.entity.Cliente;
import org.mapstruct.*;
import org.mapstruct.Builder;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        builder = @Builder(disableBuilder = true))
public interface ClienteMapper {

    @Mapping(target = "mascotas", ignore = true)
    @Mapping(target = "facturas", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    Cliente toEntity(ClienteRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "mascotas", ignore = true)
    @Mapping(target = "facturas", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    void updateEntity(ClienteRequest request, @MappingTarget Cliente cliente);
}
