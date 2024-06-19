package com.midel.dto.event;

import com.midel.dto.user.UserResponseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class PrivateAccessEventResponseDto extends EventResponseDto {

    private Set<UserResponseDto> usersWithInvite;

}
