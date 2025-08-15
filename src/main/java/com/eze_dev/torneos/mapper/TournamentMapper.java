package com.eze_dev.torneos.mapper;

import com.eze_dev.torneos.dto.request.create.TournamentCreateDto;
import com.eze_dev.torneos.dto.response.TournamentResponseDto;
import com.eze_dev.torneos.dto.request.update.TournamentUpdateDto;
import com.eze_dev.torneos.model.Tournament;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = { PairMapper.class })
public interface TournamentMapper {

    @Mapping(target = "status", ignore = true)
    Tournament toEntity(TournamentCreateDto tournamentCreateDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(TournamentUpdateDto tournamentUpdateDto, @MappingTarget Tournament entity);

    TournamentResponseDto toDto(Tournament tournament);

    List<TournamentResponseDto> toDtoList(List<Tournament> tournaments);
}


