package com.eze_dev.torneos.mapper;

import com.eze_dev.torneos.dto.request.create.UserCreateDto;
import com.eze_dev.torneos.dto.request.update.UserUpdateDto;
import com.eze_dev.torneos.dto.response.UserResponseDto;
import com.eze_dev.torneos.model.Role;
import com.eze_dev.torneos.model.UserEntity;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {PlayerMapper.class})
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "accountNotExpired", constant = "true")
    @Mapping(target = "accountNotLocked", constant = "true")
    @Mapping(target = "credentialNotExpired", constant = "true")
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "player", ignore = true)
    UserEntity toEntity(UserCreateDto userRegistrationDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UserUpdateDto userUpdateDto, @MappingTarget UserEntity entity);

    @Mapping(target = "roles", expression = "java(mapRoles(userEntity.getRoles()))")
    UserResponseDto toDto(UserEntity userEntity);

    List<UserResponseDto> toDtoList(List<UserEntity> users);

    default List<String> mapRoles(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return List.of();
        }
        return roles.stream()
                .map(Role::getName)
                .toList();
    }
}
