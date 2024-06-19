package com.midel.dto.chat;

import com.midel.dto.user.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponseDto {

    private UUID id;
    private String title;
    private UserResponseDto owner;
    private List<UserResponseDto> members;

}
