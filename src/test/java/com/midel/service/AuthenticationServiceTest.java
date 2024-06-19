package com.midel.service;

import com.midel.dto.JwtAuthenticationResponseDto;
import com.midel.dto.user.UserSignInRequestDto;
import com.midel.dto.user.UserSignUpRequestDto;
import com.midel.entity.User;
import com.midel.entity.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService; // Mock for UserDetailsService

    @InjectMocks
    private AuthenticationService authenticationService;

    private UserSignUpRequestDto signUpRequest;
    private UserSignInRequestDto signInRequest;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        signUpRequest = new UserSignUpRequestDto();
        signUpRequest.setUsername("testuser");
        signUpRequest.setPassword("password");

        signInRequest = new UserSignInRequestDto();
        signInRequest.setUsername("testuser");
        signInRequest.setPassword("password");

        userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(signInRequest.getUsername())
                .password(signInRequest.getPassword())
                .roles(Role.ROLE_USER.name().replace("ROLE_", ""))
                .build();
    }

    @Test
    void testSignUp() {
        when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        JwtAuthenticationResponseDto response = authenticationService.signUp(signUpRequest);

        verify(userService).create(any(User.class));
        verify(jwtService).generateToken(any(User.class));

        assertEquals("jwtToken", response.getToken());
    }

    @Test
    void testSignIn() {
        when(userService.userDetailsService()).thenReturn(userDetailsService);
        when(userService.userDetailsService().loadUserByUsername(signInRequest.getUsername())).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwtToken");

        JwtAuthenticationResponseDto response = authenticationService.signIn(signInRequest);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService.userDetailsService()).loadUserByUsername(signInRequest.getUsername());
        verify(jwtService).generateToken(any(UserDetails.class));

        assertEquals("jwtToken", response.getToken());
    }
}