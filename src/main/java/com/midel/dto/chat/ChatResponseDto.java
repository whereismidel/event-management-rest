package com.midel.dto.chat;

import com.midel.dto.user.UserResponseDto;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ChatResponseDto {

    private UUID id;
    private String title;
    private UserResponseDto owner;
    private List<UserResponseDto> members;

}
