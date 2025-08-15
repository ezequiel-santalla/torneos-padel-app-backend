package com.eze_dev.torneos.mapper;

import com.eze_dev.torneos.dto.response.PlayerSummaryResponseDto;
import com.eze_dev.torneos.model.Player;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlayerSummaryMapper {

    PlayerSummaryResponseDto toDto(Player player);

    List<PlayerSummaryResponseDto> toDtoList(List<Player> players);
}
