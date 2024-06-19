package com.midel.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRequestDto {

    @NotNull(message = "Friend id must be specified.")
    private Long userId;

}
