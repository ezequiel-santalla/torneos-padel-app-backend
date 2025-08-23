package com.eze_dev.torneos.mapper;

import com.eze_dev.torneos.dto.request.create.UserCreateDto;
import com.eze_dev.torneos.dto.request.update.PlayerUpdateDto;
import com.eze_dev.torneos.dto.response.PlayerResponseDto;
import com.eze_dev.torneos.dto.response.PlayerSummaryResponseDto;
import com.eze_dev.torneos.model.Player;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PlayerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userEntity", ignore = true) // Se establecerá en el servicio
    Player toEntityFromRegistration(UserCreateDto userRegistrationDto);

    PlayerResponseDto toDto(Player player);
    PlayerSummaryResponseDto toSummaryDto(Player player);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(PlayerUpdateDto dto, @MappingTarget Player entity);
}
