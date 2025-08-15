package com.eze_dev.torneos.controller;

import com.eze_dev.torneos.dto.request.create.AuthLoginRequestDto;
import com.eze_dev.torneos.dto.request.create.UserCreateDto;
import com.eze_dev.torneos.dto.response.AuthLoginResponseDto;
import com.eze_dev.torneos.dto.response.UserResponseDto;
import com.eze_dev.torneos.service.implementations.UserDetailsServiceImpl;
import com.eze_dev.torneos.service.interfaces.IUserService;
import com.eze_dev.torneos.utils.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserDetailsServiceImpl userDetailsService;
    private final IUserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponseDto> login(@RequestBody @Valid AuthLoginRequestDto authLoginRequestDto) {
        return ResponseEntity.ok(userDetailsService.login(authLoginRequestDto));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserCreateDto userCreateDto) {
        return ResponseEntity.ok(userService.register(userCreateDto));
    }

    @GetMapping("/check-status")
    public ResponseEntity<AuthLoginResponseDto> checkUserStatus(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(jwtUtils.validateTokenAndCreateResponse(token));

    }
}
