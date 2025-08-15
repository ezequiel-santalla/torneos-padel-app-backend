package com.eze_dev.torneos.mapper;

import com.eze_dev.torneos.dto.request.create.PermissionCreateDto;
import com.eze_dev.torneos.dto.request.update.PermissionUpdateDto;
import com.eze_dev.torneos.dto.response.PermissionResponseDto;
import com.eze_dev.torneos.model.Permission;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    // Crear entidad desde DTO
    Permission toEntity(PermissionCreateDto permissionCreateDto);

    // Actualizar entidad ignorando nulos
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(PermissionUpdateDto permissionUpdateDto, @MappingTarget Permission entity);

    // Mapear entidad a DTO respuesta
    PermissionResponseDto toDto(Permission permission);

    // Mapear lista de entidades a lista de DTOs
    List<PermissionResponseDto> toDtoList(List<Permission> permissions);
}
