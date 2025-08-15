package com.eze_dev.torneos.mapper;

import com.eze_dev.torneos.dto.request.create.UserCreateDto;
import com.eze_dev.torneos.dto.response.PlayerResponseDto;
import com.eze_dev.torneos.model.Player;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlayerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userEntity", ignore = true) // Se establecerá en el servicio
    Player toEntityFromRegistration(UserCreateDto userRegistrationDto);

    PlayerResponseDto toDto(Player player);
}
