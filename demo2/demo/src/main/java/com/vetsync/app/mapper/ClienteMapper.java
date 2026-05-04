package com.vetsync.app.mapper;

import com.vetsync.app.dto.request.ClienteRequest;
import com.vetsync.app.entity.Cliente;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    Cliente toEntity(ClienteRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(ClienteRequest request, @MappingTarget Cliente cliente);
}
