package com.eze_dev.torneos.dto.request.update;

import com.eze_dev.torneos.types.GenderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerUpdateDto {

    private String name;

    private String lastName;

    private GenderType genderType;

    private String dni;

    private String phoneNumber;
}
