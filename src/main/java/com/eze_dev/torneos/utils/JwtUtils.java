package com.eze_dev.torneos.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.eze_dev.torneos.dto.response.AuthLoginResponseDto;
import com.eze_dev.torneos.model.UserEntity;
import com.eze_dev.torneos.model.Role;
import com.eze_dev.torneos.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtils {

    private final UserRepository userRepository;

    @Value("${app.security.jwt.secret-key}")
    private String secretKey;

    @Value("${app.security.jwt.user.generator}")
    private String userGenerator;

    @Value("${app.security.jwt.expiration:1800}")
    private long jwtExpirationInSeconds;

    private Algorithm algorithm;
    private JWTVerifier jwtVerifier;

    @PostConstruct
    public void init() {
        if (!StringUtils.hasText(secretKey)) {
            throw new IllegalArgumentException("JWT secret key cannot be null or empty");
        }
        if (!StringUtils.hasText(userGenerator)) {
            throw new IllegalArgumentException("JWT user generator cannot be null or empty");
        }

        algorithm = Algorithm.HMAC256(secretKey);
        jwtVerifier = JWT.require(algorithm)
                .withIssuer(userGenerator)
                .build();

        log.info("JWT Utils initialized with expiration: {} seconds", jwtExpirationInSeconds);
    }

    public String generateToken(Authentication authentication) {
        if (authentication == null || !StringUtils.hasText(authentication.getName())) {
            throw new IllegalArgumentException("Invalid authentication or username");
        }

        String username = authentication.getName();

        String authorities = authentication.getAuthorities() != null ?
                authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .filter(StringUtils::hasText)
                        .collect(Collectors.joining(",")) : "";

        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(jwtExpirationInSeconds);

        try {
            String token = JWT.create()
                    .withIssuer(userGenerator)
                    .withSubject(username)
                    .withClaim("authorities", authorities)
                    .withIssuedAt(Date.from(now))
                    .withExpiresAt(Date.from(expiration))
                    .withJWTId(UUID.randomUUID().toString())
                    .withNotBefore(Date.from(now))
                    .sign(algorithm);

            log.debug("JWT token generated successfully for user: {} with authorities: {}",
                    username, authorities);
            return token;

        } catch (JWTCreationException e) {
            log.error("JWT creation failed for user: {}", username, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error generating JWT token for user: {}", username, e);
            throw new JWTCreationException("Failed to generate JWT token", e);
        }
    }

    public DecodedJWT decodeToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("JWT token cannot be null or empty");
        }

        try {
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            log.debug("JWT token decoded successfully for user: {}", decodedJWT.getSubject());
            return decodedJWT;

        } catch (JWTVerificationException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            throw new JWTVerificationException("Invalid JWT token", e);
        }
    }

    public boolean isTokenValid(String token) {
        try {
            if (!StringUtils.hasText(token)) {
                return false;
            }

            DecodedJWT decodedJWT = decodeToken(token);

            if (decodedJWT.getExpiresAt().before(new Date())) {
                log.debug("Token is expired for user: {}", decodedJWT.getSubject());
                return false;
            }

            return true;

        } catch (JWTVerificationException e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.debug("Unexpected error validating token: {}", e.getMessage());
            return false;
        }
    }

    public AuthLoginResponseDto validateTokenAndCreateResponse(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) {
            log.debug("Authorization header is null or empty");
            return null;
        }

        String token = authorizationHeader.startsWith("Bearer ") ?
                authorizationHeader.substring(7) : authorizationHeader;

        if (!isTokenValid(token)) {
            log.debug("Token is invalid or expired");
            return null;
        }

        try {
            DecodedJWT decodedToken = decodeToken(token);
            String email = getUsernameFromToken(decodedToken);

            UserEntity userEntity = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

            if (userEntity.getPlayer() == null) {
                throw new IllegalStateException("User has no associated player data");
            }
            
            AuthLoginResponseDto response = new AuthLoginResponseDto(
                    userEntity.getPlayer().getId(),
                    userEntity.getPlayer().getName(),
                    userEntity.getPlayer().getLastName(),
                    userEntity.getRoles().stream()
                            .map(Role::getName)
                            .toList(),
                    token
            );

            log.debug("Token validation successful for user: {}", email);
            return response;

        } catch (Exception e) {
            log.error("Error creating auth response for valid token", e);
            return null;
        }
    }

    public String getUsernameFromToken(DecodedJWT token) {
        if (token == null) {
            throw new IllegalArgumentException("Decoded JWT cannot be null");
        }
        return token.getSubject();
    }

    public Claim getClaimFromToken(DecodedJWT token, String claimName) {
        if (token == null) {
            throw new IllegalArgumentException("Decoded JWT cannot be null");
        }
        if (!StringUtils.hasText(claimName)) {
            throw new IllegalArgumentException("Claim name cannot be null or empty");
        }
        return token.getClaim(claimName);
    }

    public Map<String, Claim> getAllClaimsFromToken(DecodedJWT token) {
        if (token == null) {
            throw new IllegalArgumentException("Decoded JWT cannot be null");
        }
        return token.getClaims();
    }
}