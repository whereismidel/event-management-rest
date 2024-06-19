package com.midel.controller;

import com.midel.dto.Mapper;
import com.midel.dto.chat.ChatCreateRequestDto;
import com.midel.dto.user.UserRequestDto;
import com.midel.dto.user.UserResponseDto;
import com.midel.response.ErrorResponse;
import com.midel.response.RestResponse;
import com.midel.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("chats")
@Tag(name = "Chat management")
@SecurityRequirement(name = "bearerAuth")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "Create a chat as an authorized user")
    @PostMapping
    public ResponseEntity<?> createChat(@RequestBody @Valid ChatCreateRequestDto chatCreateRequestDto) {

        return new RestResponse(
                HttpStatus.CREATED,
                chatService.create(chatCreateRequestDto.getTitle())
        ).getResponseEntity();

    }

    @Operation(summary = "Get all chats of an authorized user")
    @GetMapping
    public ResponseEntity<?> getMyChats() {

        return new RestResponse(
                HttpStatus.OK,
                chatService.getMy()
        ).getResponseEntity();

    }

    @Operation(summary = "Get chat by id of an authorized user")
    @GetMapping("{chatId}")
    public ResponseEntity<?> getChat(@PathVariable UUID chatId) {

        return new RestResponse(
                HttpStatus.OK,
                Mapper.INSTANCE.chatToChatResponse(chatService.getAuthUserChatById(chatId))
        ).getResponseEntity();

    }

    @Operation(summary = "Add a participant to the chat of an authorized user")
    @PostMapping("{chatId}/invite")
    public ResponseEntity<?> addChatMember(@PathVariable UUID chatId, @RequestBody UserRequestDto userRequestDto) {

        chatService.addChatMember(chatId, userRequestDto.getUserId());
        return new ResponseEntity<>(HttpStatus.OK);

    }

}
