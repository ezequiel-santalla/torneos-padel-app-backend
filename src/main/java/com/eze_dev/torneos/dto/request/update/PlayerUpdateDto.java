package com.eze_dev.torneos.dto.request.update;

import com.eze_dev.torneos.types.GenderType;

public record PlayerUpdateDto(
        String name,
        String lastName,
        GenderType genderType,
        String dni,
        String phoneNumber
) {}
