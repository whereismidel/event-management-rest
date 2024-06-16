package com.midel.dto;

import com.midel.entity.enums.Role;
import lombok.Data;

import java.util.List;

@Data
public class UserWithFriendsDto {

    private Long id;
    private String username;
    private Role role;
    private List<UserDto> friends;

}
