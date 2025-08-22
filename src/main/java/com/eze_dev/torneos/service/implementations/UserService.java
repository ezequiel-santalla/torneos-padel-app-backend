package com.eze_dev.torneos.service.implementations;

import com.eze_dev.torneos.dto.request.create.UserCreateDto;
import com.eze_dev.torneos.dto.request.update.UserUpdateDto;
import com.eze_dev.torneos.dto.response.UserResponseDto;
import com.eze_dev.torneos.mapper.PlayerMapper;
import com.eze_dev.torneos.mapper.UserMapper;
import com.eze_dev.torneos.model.Player;
import com.eze_dev.torneos.model.Role;
import com.eze_dev.torneos.model.UserEntity;
import com.eze_dev.torneos.repository.PlayerRepository;
import com.eze_dev.torneos.repository.RoleRepository;
import com.eze_dev.torneos.repository.UserRepository;
import com.eze_dev.torneos.service.interfaces.IUserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDto register(UserCreateDto userCreateDto) {
        log.info("Attempting to register user with email: {}", userCreateDto.email());

        if (userRepository.existsByEmail(userCreateDto.email())) {
            log.warn("Registration failed - Email already exists: {}", userCreateDto.email());

            throw new EntityExistsException("User with email " + userCreateDto.email() + " already exists");
        }

        if (playerRepository.existsByDni(userCreateDto.dni())) {
            log.warn("Registration failed - DNI already exists: {}", userCreateDto.dni());
            throw new EntityExistsException("Player with DNI " + userCreateDto.dni() + " already exists");
        }

        if (!userCreateDto.password().equals(userCreateDto.confirmPassword())) {
            log.warn("Registration failed - Passwords do not match for email: {}", userCreateDto.email());
            throw new IllegalArgumentException("Passwords do not match");
        }

        Player player = playerMapper.toEntityFromRegistration(userCreateDto);

        UserEntity user = userMapper.toEntity(userCreateDto);
        user.setPassword(passwordEncoder.encode(userCreateDto.password()));

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role USER not found"));

        user.setRoles(Set.of(userRole));

        user.setPlayer(player);
        player.setUserEntity(user);

        UserEntity savedUser = userRepository.save(user);

        log.info("User registered successfully with email: {} and ID: {}",
                savedUser.getEmail(), savedUser.getId());

        return userMapper.toDto(savedUser);
    }

    @Override
    public List<UserResponseDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserResponseDto getById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }

    @Override
    public UserResponseDto getByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }

    @Override
    public UserResponseDto update(UUID id, UserUpdateDto userUpdateDto) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    userMapper.updateEntityFromDto(userUpdateDto, existingUser);

                    if (userUpdateDto.player() != null && existingUser.getPlayer() != null) {
                        playerMapper.updateEntityFromDto(userUpdateDto.player(), existingUser.getPlayer());
                    }

                    return userMapper.toDto(userRepository.save(existingUser));
                })
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }


    @Override
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with ID: " + id);
        }

        userRepository.deleteById(id);
    }

    @Override
    public UserResponseDto toggleUserStatus(UUID id) {
        return null;
    }
}
