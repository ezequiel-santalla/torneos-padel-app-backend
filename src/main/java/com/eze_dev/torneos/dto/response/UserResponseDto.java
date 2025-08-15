package com.eze_dev.torneos.dto.response;

import java.util.List;
import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String email,
        boolean enabled,
        List<String> roles,
        PlayerResponseDto player) {}
