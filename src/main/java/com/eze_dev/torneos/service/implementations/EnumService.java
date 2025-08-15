package com.eze_dev.torneos.service.implementations;

import com.eze_dev.torneos.service.interfaces.IEnumService;
import com.eze_dev.torneos.types.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EnumService implements IEnumService {

    @Override
    public List<String> getCategoryTypes() {
        return Arrays.stream(CategoryType.values())
                .map(Enum::name)
                .toList();
    }

    @Override
    public List<String> getGenderTypes() {
        return Arrays.stream(GenderType.values())
                .map(Enum::name)
                .toList();
    }

    @Override
    public List<String> getMatchStatuses() {
        return Arrays.stream(MatchStatus.values())
                .map(Enum::name)
                .toList();
    }

    @Override
    public List<String> getTournamentStatuses() {
        return Arrays.stream(TournamentStatus.values())
                .map(Enum::name)
                .toList();
    }

    @Override
    public List<String> getTournamentTypes() {
        return Arrays.stream(TournamentType.values())
                .map(Enum::name)
                .toList();
    }

    @Override
    public List<String> getWinningMatchRuleTypes() {
        return Arrays.stream(WinningMatchRuleType.values())
                .map(Enum::name)
                .toList();
    }

    @Override
    public Map<String, List<String>> getAllEnums() {
        Map<String, List<String>> enums = new HashMap<>();
        enums.put("categoryTypes", getCategoryTypes());
        enums.put("genderTypes", getGenderTypes());
        enums.put("matchStatuses", getMatchStatuses());
        enums.put("tournamentStatuses", getTournamentStatuses());
        enums.put("tournamentTypes", getTournamentTypes());
        enums.put("winningMatchRuleTypes", getWinningMatchRuleTypes());
        return enums;
    }

    @Override
    public List<Map<String, String>> getCategoryTypesWithInfo() {
        return Arrays.stream(CategoryType.values())
                .map(this::createEnumInfo)
                .toList();
    }

    @Override
    public List<Map<String, String>> getGenderTypesWithInfo() {
        return Arrays.stream(GenderType.values())
                .map(this::createEnumInfo)
                .toList();
    }

    @Override
    public List<Map<String, String>> getMatchStatusesWithInfo() {
        return Arrays.stream(MatchStatus.values())
                .map(this::createEnumInfo)
                .toList();
    }

    @Override
    public List<Map<String, String>> getTournamentStatusesWithInfo() {
        return Arrays.stream(TournamentStatus.values())
                .map(this::createEnumInfo)
                .toList();
    }

    @Override
    public List<Map<String, String>> getTournamentTypesWithInfo() {
        return Arrays.stream(TournamentType.values())
                .map(this::createEnumInfo)
                .toList();
    }

    @Override
    public List<Map<String, String>> getWinningMatchRuleTypesWithInfo() {
        return Arrays.stream(WinningMatchRuleType.values())
                .map(this::createEnumInfo)
                .toList();
    }

    private Map<String, String> createEnumInfo(Enum<?> enumValue) {
        Map<String, String> info = new HashMap<>();
        info.put("value", enumValue.name());
        info.put("label", formatEnumLabel(enumValue.name()));
        return info;
    }

    private String formatEnumLabel(String enumName) {
        return enumName.replace("_", " ");
    }
}
