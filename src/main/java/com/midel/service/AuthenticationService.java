package com.midel.service;

import com.midel.dto.JwtAuthenticationResponseDto;
import com.midel.dto.user.UserSignInRequestDto;
import com.midel.dto.user.UserSignUpRequestDto;
import com.midel.entity.User;
import com.midel.entity.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationResponseDto signUp(UserSignUpRequestDto request) {

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        userService.create(user);

        String jwt = jwtService.generateToken(user);

        return new JwtAuthenticationResponseDto(jwt);
    }

    public JwtAuthenticationResponseDto signIn(UserSignInRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
        );

        UserDetails user = userService
                .userDetailsService()
                .loadUserByUsername(request.getUsername());

        String jwt = jwtService.generateToken(user);

        return new JwtAuthenticationResponseDto(jwt);
    }

}