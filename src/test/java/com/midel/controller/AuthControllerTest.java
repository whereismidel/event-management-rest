package com.midel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midel.dto.JwtAuthenticationResponseDto;
import com.midel.dto.user.UserSignInRequestDto;
import com.midel.dto.user.UserSignUpRequestDto;
import com.midel.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    public void testSignUp() throws Exception {
        // Arrange
        UserSignUpRequestDto userSignUpRequestDto = new UserSignUpRequestDto();
        userSignUpRequestDto.setUsername("testuser");
        userSignUpRequestDto.setPassword("testpassword");

        JwtAuthenticationResponseDto jwtAuthenticationResponseDto = new JwtAuthenticationResponseDto("test_token");

        when(authenticationService.signUp(any(UserSignUpRequestDto.class))).thenReturn(jwtAuthenticationResponseDto);

        // Act and Assert
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userSignUpRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test_token"));
    }

    @Test
    public void testSignIn() throws Exception {
        // Arrange
        UserSignInRequestDto userSignInRequestDto = new UserSignInRequestDto();
        userSignInRequestDto.setUsername("testuser");
        userSignInRequestDto.setPassword("testpassword");

        JwtAuthenticationResponseDto jwtAuthenticationResponseDto = new JwtAuthenticationResponseDto("test_token");

        when(authenticationService.signIn(any(UserSignInRequestDto.class))).thenReturn(jwtAuthenticationResponseDto);

        // Act and Assert
        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userSignInRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test_token"));
    }
}