package com.eze_dev.torneos.mapper;

import com.eze_dev.torneos.dto.request.create.RoleCreateDto;
import com.eze_dev.torneos.dto.request.update.RoleUpdateDto;
import com.eze_dev.torneos.dto.response.RoleResponseDto;
import com.eze_dev.torneos.model.Role;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PermissionMapper.class})
public interface RoleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    Role toEntity(RoleCreateDto roleCreateDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "permissions", ignore = true)
    void updateEntityFromDto(RoleUpdateDto roleUpdateDto, @MappingTarget Role entity);

    RoleResponseDto toDto(Role role);

    List<RoleResponseDto> toDtoList(List<Role> roles);
}
