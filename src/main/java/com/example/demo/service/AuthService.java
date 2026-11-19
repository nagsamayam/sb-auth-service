package com.example.demo.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.config.JwtProperties;
import com.example.demo.dto.AuthTokenResponse;
import com.example.demo.dto.UserLoginRequestDto;
import com.example.demo.dto.UserRegisterRequestDto;
import com.example.demo.dto.UserRegistrationResponseDto;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;
    private final RefreshTokenService refreshTokenService;

    public UserRegistrationResponseDto register(UserRegisterRequestDto userRegisterRequestDto) {

        Role role = roleRepository.findByName("ROLE_USER").get();

        User user = new User();
        user.setUsername(userRegisterRequestDto.getUsername());
        user.setPassword(passwordEncoder.encode(userRegisterRequestDto.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        return UserRegistrationResponseDto.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .build();
    }

    public AuthTokenResponse login(UserLoginRequestDto requestDto) {
        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(requestDto.getUsername(), requestDto.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationRequest);

        return AuthTokenResponse.builder()
                .accessToken(jwtService.generateToken(authentication))
                .tokenType("Bearer")
                .expiresIn(jwtProperties.expiry())
                .refreshToken(jwtService.generateRefreshToken(authentication))
                .refreshTokenExpiresIn(jwtProperties.refreshExpiry())
                .build();
    }

    public AuthTokenResponse refreshToken(Authentication authentication, String refreshToken) {
        RefreshTokenService.RefreshTokenResponse newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken);

        return AuthTokenResponse.builder()
                .accessToken(jwtService.generateToken(authentication))
                .tokenType("Bearer")
                .expiresIn(jwtProperties.expiry())
                .refreshToken(newRefreshToken.rawToken())
                .refreshTokenExpiresIn(jwtProperties.refreshExpiry())
                .build();
    }
}
