package com.eze_dev.torneos.mapper;

import com.eze_dev.torneos.dto.request.create.MatchCreateDto;
import com.eze_dev.torneos.dto.response.MatchResponseDto;
import com.eze_dev.torneos.dto.request.update.MatchResultUpdateDto;
import com.eze_dev.torneos.model.Match;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = { PairMapper.class })
public interface MatchMapper {

    Match toEntity(MatchCreateDto matchCreateDto);

    @Mapping(source = "tournament.id", target = "tournamentId")
    MatchResponseDto toDto(Match match);

    List<MatchResponseDto> toDtoList(List<Match> matches);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateResultFromDto(MatchResultUpdateDto dto, @MappingTarget Match match);
}
