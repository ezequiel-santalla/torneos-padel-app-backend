package com.eze_dev.torneos.mapper;

import com.eze_dev.torneos.dto.response.PairStandingResponseDto;
import com.eze_dev.torneos.strategy.tournament.PairStanding;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PairStandingMapper {

    @Mapping(source = "pair.id", target = "pairId")
    @Mapping(source = "pair.teamName", target = "pairName")
    PairStandingResponseDto toDto(PairStanding standing);
}


