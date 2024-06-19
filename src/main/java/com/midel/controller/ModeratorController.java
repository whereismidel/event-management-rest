package com.midel.controller;

import com.midel.entity.enums.Status;
import com.midel.response.RestResponse;
import com.midel.service.ChatService;
import com.midel.service.EventService;
import com.midel.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("moderation")
@Tag(name = "Moderator controls")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('MODERATOR')")
public class ModeratorController {

    private final UserService userService;
    private final ChatService chatService;
    private final EventService eventService;
    private final EventController eventController;

    @Operation(summary = "Get all users")
    @GetMapping("users")
    public ResponseEntity<?> getAllUsers() {
        return new RestResponse(
                HttpStatus.OK,
                userService.getAll()
        ).getResponseEntity();
    }

    @Operation(summary = "Get chats of all users")
    @GetMapping("chats")
    public ResponseEntity<?> getAllChats() {
        return new RestResponse(
                HttpStatus.OK,
                chatService.getAll()
        ).getResponseEntity();
    }

    @Operation(summary = "Get all events of all users")
    @GetMapping("events")
    public ResponseEntity<?> getAllEvents(@RequestParam(name = "status", required = false) String status) {
        if (status != null && !status.isEmpty()) {
            try {
                Status statusEnum = Status.valueOf(status.toUpperCase());
                return new RestResponse(
                        HttpStatus.OK,
                        eventService.getAllByStatus(statusEnum)
                ).getResponseEntity();
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status parameter");
            }
        } else {
            return new RestResponse(
                    HttpStatus.OK,
                    eventService.getAll()
            ).getResponseEntity();
        }
    }

    @Operation(summary = "Get any event.")
    @GetMapping("events/{eventId}")
    public ResponseEntity<?> getEvent(@PathVariable UUID eventId) {
        return new RestResponse(
                HttpStatus.OK,
                eventService.getEventDtoFunction(eventService.getEvent(eventId))
        ).getResponseEntity();
    }

    @Operation(summary = "Approve event")
    @GetMapping("events/{eventId}/approve")
    public ResponseEntity<?> approveEvent(@PathVariable UUID eventId) {
        eventService.changeEventStatus(eventId, Status.OPENED);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Reject event")
    @GetMapping("events/{eventId}/reject")
    public ResponseEntity<?> rejectEvent(@PathVariable UUID eventId) {
        eventService.changeEventStatus(eventId, Status.REJECTED);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
