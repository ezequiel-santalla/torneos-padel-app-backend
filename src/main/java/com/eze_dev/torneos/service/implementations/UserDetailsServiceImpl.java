package com.eze_dev.torneos.service.implementations;

import com.eze_dev.torneos.dto.request.create.AuthLoginRequestDto;
import com.eze_dev.torneos.dto.response.AuthLoginResponseDto;
import com.eze_dev.torneos.model.Role;
import com.eze_dev.torneos.model.UserEntity;
import com.eze_dev.torneos.repository.UserRepository;
import com.eze_dev.torneos.utils.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);

        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", email);

                    return new UsernameNotFoundException("User not found: " + email);
                });

        Collection<GrantedAuthority> authorities = getAuthorities(userEntity);

        log.debug("User {} loaded with {} authorities", email, authorities.size());

        return User.builder()
                .username(userEntity.getEmail())
                .password(userEntity.getPassword())
                .disabled(!userEntity.isEnabled())
                .accountExpired(!userEntity.isAccountNotExpired())
                .credentialsExpired(!userEntity.isCredentialNotExpired())
                .accountLocked(!userEntity.isAccountNotLocked())
                .authorities(authorities)
                .build();
    }

    @Transactional
    public AuthLoginResponseDto login(AuthLoginRequestDto loginRequestDto) {
        String email = loginRequestDto.email();
        String password = loginRequestDto.password();

        Authentication authentication = this.authenticateUser(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        if (userEntity.getPlayer() == null) {
            throw new IllegalStateException("User has no associated player data");
        }

        String jwtToken = jwtUtils.generateToken(authentication);

        return new AuthLoginResponseDto(
                userEntity.getPlayer().getId(),
                userEntity.getPlayer().getName(),
                userEntity.getPlayer().getLastName(),
                userEntity.getRoles().stream()
                        .map(Role::getName)
                        .toList(),
                jwtToken
        );
    }

    public Authentication authenticateUser(String email, String password) {
        UserDetails userDetails = loadUserByUsername(email);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            log.warn("Authentication failed - Invalid password attempt");
            throw new BadCredentialsException("Invalid email or password");
        }

        log.debug("User authenticated successfully: {}", email);
        return new UsernamePasswordAuthenticationToken(email, userDetails.getPassword(), userDetails.getAuthorities());
    }

    private Collection<GrantedAuthority> getAuthorities(UserEntity user) {
        return user.getRoles().stream()
                .flatMap(role -> Stream.concat(
                        Stream.of(new SimpleGrantedAuthority("ROLE_" + role.getName())),
                        role.getPermissions().stream()
                                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                ))
                .collect(Collectors.toSet());
    }
}
