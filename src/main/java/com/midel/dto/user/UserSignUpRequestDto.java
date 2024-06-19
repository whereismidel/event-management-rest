package com.midel.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserSignUpRequestDto {

    @NotNull(message = "Username must be specified.")
    @NotBlank(message = "Username must be not empty string.")
    private String username;

    @NotNull(message = "Password must be specified.")
    @NotBlank(message = "Password must be not empty string.")
    private String password;

}
