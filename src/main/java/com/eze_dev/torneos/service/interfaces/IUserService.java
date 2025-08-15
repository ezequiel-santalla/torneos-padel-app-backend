package com.eze_dev.torneos.service.interfaces;

import com.eze_dev.torneos.dto.request.create.UserCreateDto;
import com.eze_dev.torneos.dto.request.update.UserUpdateDto;
import com.eze_dev.torneos.dto.response.UserResponseDto;

import java.util.List;
import java.util.UUID;

public interface IUserService {

    UserResponseDto register(UserCreateDto userRegistrationDto);
    List<UserResponseDto> getAll();
    UserResponseDto getById(UUID id);
    UserResponseDto getByEmail(String email);
    UserResponseDto update(UUID id, UserUpdateDto userUpdateDto);
    void delete(UUID id);
    UserResponseDto toggleUserStatus(UUID id);
}
