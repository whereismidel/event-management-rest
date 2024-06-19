package com.midel.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChatCreateRequestDto {

    @NotNull(message = "Title must be specified.")
    @NotBlank(message = "Title must be not empty string.")
    private String title;

}
