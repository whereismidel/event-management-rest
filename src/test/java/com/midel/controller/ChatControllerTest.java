package com.midel.controller;

import com.midel.dto.chat.ChatCreateRequestDto;
import com.midel.dto.chat.ChatResponseDto;
import com.midel.dto.user.UserRequestDto;
import com.midel.dto.user.UserResponseDto;
import com.midel.entity.Chat;
import com.midel.entity.User;
import com.midel.response.RestResponse;
import com.midel.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private ChatService chatService;

    @InjectMocks
    private ChatController chatController;

    private Chat chat;
    private ChatResponseDto chatResponseDto;

    @BeforeEach
    void setUp() {
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("testUser");

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(currentUser.getId());
        userResponseDto.setUsername(currentUser.getUsername());

        chat = new Chat();
        chat.setId(UUID.randomUUID());
        chat.setTitle("Test Chat");

        chatResponseDto = new ChatResponseDto(chat.getId(), chat.getTitle(), userResponseDto, Collections.emptyList());
    }

    @Test
    void createChat_ReturnsCreatedChatResponse() {
        ChatCreateRequestDto requestDto = new ChatCreateRequestDto();
        requestDto.setTitle("New Chat");

        when(chatService.create(requestDto.getTitle())).thenReturn(chatResponseDto);

        ResponseEntity<?> response = chatController.createChat(requestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(chatResponseDto, ((RestResponse) response.getBody()).getData());
    }

    @Test
    void getMyChats_ReturnsUserChats() {
        when(chatService.getMy()).thenReturn(Set.of(chatResponseDto));

        ResponseEntity<?> response = chatController.getMyChats();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Set.of(chatResponseDto), ((RestResponse) response.getBody()).getData());
    }

    @Test
    void addChatMember_CallsChatServiceAddChatMember() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUserId(2L);

        chatController.addChatMember(chat.getId(), userRequestDto);

        verify(chatService, times(1)).addChatMember(eq(chat.getId()), any(Long.class));
    }

    @Test
    void addChatMember_ReturnsOkStatus() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUserId(2L);

        ResponseEntity<?> response = chatController.addChatMember(chat.getId(), userRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getChat_ShouldReturnChatResponse() {
        // Arrange
        UUID chatId = UUID.randomUUID();
        Chat chat = new Chat();
        ChatResponseDto chatResponse = new ChatResponseDto();

        when(chatService.getAuthUserChatById(chatId)).thenReturn(chat);

        // Act
        ResponseEntity<?> response = chatController.getChat(chatId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(chatResponse, ((RestResponse) response.getBody()).getData());
    }
}