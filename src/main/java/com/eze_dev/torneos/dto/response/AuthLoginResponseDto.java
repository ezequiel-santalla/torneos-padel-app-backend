package com.eze_dev.torneos.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
@JsonPropertyOrder({"playerId", "name", "lastName", "roles", "token"})
public record AuthLoginResponseDto(
        UUID playerId,
        String name,
        String lastName,
        List<String> roles,
        String token) {}
