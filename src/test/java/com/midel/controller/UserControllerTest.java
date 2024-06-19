package com.midel.controller;

import com.midel.dto.user.UserRequestDto;
import com.midel.dto.user.UserResponseDto;
import com.midel.response.RestResponse;
import com.midel.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private List<UserResponseDto> friends;

    @BeforeEach
    void setUp() {
        friends = Arrays.asList(
                new UserResponseDto(1L, "user1"),
                new UserResponseDto(2L, "user2")
        );
    }

    @Test
    void getFriends_ReturnsListOfFriends() {
        when(userService.getFriends()).thenReturn(friends);

        ResponseEntity<?> response = userController.getFriends();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(friends, ((RestResponse) response.getBody()).getData());
    }

    @Test
    void addFriend_CallsUserServiceAddFriend() {
        UserRequestDto userRequestDto = new UserRequestDto(3L);

        userController.addFriend(userRequestDto);

        verify(userService, times(1)).addFriend(3L);
    }
}