package com.midel.controller;

import com.midel.dto.JwtAuthenticationResponseDto;
import com.midel.dto.UserSignInDto;
import com.midel.dto.UserSignUpDto;
import com.midel.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication")
public class AuthController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "User registration")
    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponseDto> signUp(@RequestBody @Valid UserSignUpDto request) {
        return ResponseEntity.ok(authenticationService.signUp(request));
    }

    @Operation(summary = "User authentication")
    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponseDto> signIn(@RequestBody @Valid UserSignInDto request) {
        return ResponseEntity.ok(authenticationService.signIn(request));
    }

}
