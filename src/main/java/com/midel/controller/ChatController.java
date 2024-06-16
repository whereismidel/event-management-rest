package com.midel.controller;

import com.midel.dto.ChatCreateDto;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("chats")
@Tag(name = "Chat management")
@SecurityRequirement(name = "bearerAuth")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "Get chats of all users")
    @GetMapping
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<?> getAllChats() {
        return new RestResponse(
                HttpStatus.OK,
                chatService.getAll()
        ).getResponseEntity();
    }

    @Operation(summary = "Create a chat as an authorized user")
    @PostMapping
    public ResponseEntity<?> createChat(@RequestBody @Valid ChatCreateDto chatCreateDto) {
        return new RestResponse(
                HttpStatus.OK,
                chatService.create(chatCreateDto.getTitle())
        ).getResponseEntity();
    }

    @Operation(summary = "Get all chats of an authorized user")
    @GetMapping("my")
    public ResponseEntity<?> getMyChats() {
        return new RestResponse(
                HttpStatus.OK,
                chatService.getMyChats()
        ).getResponseEntity();
    }

    @Operation(summary = "Get chat by id of an authorized user")
    @GetMapping("{chatId}")
    public ResponseEntity<?> getChat(@PathVariable UUID chatId) {
        try {
            return new RestResponse(
                    HttpStatus.OK,
                    chatService.getAuthUserChatById(chatId)
            ).getResponseEntity();
        } catch (Exception e) {
            return new ErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage()
            ).getResponseEntity();
        }
    }


    @Operation(summary = "Add a participant to the chat of an authorized user")
    @PostMapping("{chatId}")
    public ResponseEntity<?> addChatMember(@PathVariable UUID chatId, @RequestBody Long userId) {
        try {
            chatService.addChatMember(chatId, userId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage()
            ).getResponseEntity();
        }
    }

}
