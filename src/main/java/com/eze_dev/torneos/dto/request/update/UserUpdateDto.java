package com.eze_dev.torneos.dto.request.update;

import com.eze_dev.torneos.types.GenderType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDto {

    @Email(message = "Email must be valid")
    private String email;

    private String password;

    private String name;
    private String lastName;
    private GenderType genderType;

    @Pattern(regexp = "\\d{8}", message = "DNI must have 8 digits")
    private String dni;

    private String phoneNumber;
}
