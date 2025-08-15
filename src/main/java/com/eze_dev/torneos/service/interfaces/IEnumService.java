package com.eze_dev.torneos.service.interfaces;

import java.util.List;
import java.util.Map;

public interface IEnumService {

    List<String> getCategoryTypes();

    List<String> getGenderTypes();

    List<String> getMatchStatuses();

    List<String> getTournamentStatuses();

    List<String> getTournamentTypes();

    List<String> getWinningMatchRuleTypes();

    Map<String, List<String>> getAllEnums();

    List<Map<String, String>> getCategoryTypesWithInfo();

    List<Map<String, String>> getGenderTypesWithInfo();

    List<Map<String, String>> getMatchStatusesWithInfo();

    List<Map<String, String>> getTournamentStatusesWithInfo();

    List<Map<String, String>> getTournamentTypesWithInfo();

    List<Map<String, String>> getWinningMatchRuleTypesWithInfo();
}
