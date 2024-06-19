package com.midel.dto.event;

import com.midel.dto.user.UserResponseDto;
import com.midel.entity.enums.EventVisibility;
import com.midel.entity.enums.Status;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class EventResponseDto {

    private UUID id;
    private String title;
    private String description;
    private EventVisibility visibility;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime expirationAt;
    private UserResponseDto owner;

}
