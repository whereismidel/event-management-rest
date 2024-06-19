package com.midel.dto.user;

import com.midel.entity.enums.Role;
import lombok.Data;

import java.util.List;

@Data
public class UserWithFriendsResponseDto {

    private Long id;
    private String username;
    private Role role;
    private List<UserResponseDto> friends;

}
