package com.midel.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ChatDto {

    private UUID id;
    private String title;
    private UserDto owner;
    private List<UserDto> members;

}
