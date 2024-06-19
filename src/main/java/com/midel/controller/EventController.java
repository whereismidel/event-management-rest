package com.midel.controller;

import com.midel.dto.event.EventCreateRequestDto;
import com.midel.dto.event.EventUpdateRequestDto;
import com.midel.dto.user.UserRequestDto;
import com.midel.response.RestResponse;
import com.midel.service.EventService;
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
@RequestMapping("events")
@Tag(name = "Event management")
@SecurityRequirement(name = "bearerAuth")
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Create an event as an authorized user")
    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody @Valid EventCreateRequestDto eventCreateRequestDto) {

        return new RestResponse(
                HttpStatus.CREATED,
                eventService.createEvent(eventCreateRequestDto)
        ).getResponseEntity();

    }

    @Operation(summary = "Get all events of an authorized user")
    @GetMapping
    public ResponseEntity<?> getMyEvents() {
        return new RestResponse(
                HttpStatus.OK,
                eventService.getMyEvents()
        ).getResponseEntity();
    }

    @Operation(summary = "Get event by id of an authorized user")
    @GetMapping("{eventId}")
    public ResponseEntity<?> getEventById(@PathVariable UUID eventId) {

        return new RestResponse(
                HttpStatus.OK,
                eventService.getEventDtoFunction(eventService.getAuthUserEventById(eventId))
        ).getResponseEntity();

    }

    @Operation(summary = "Remove event by id of an authorized user")
    @DeleteMapping("{eventId}")
    public ResponseEntity<?> removeEvent(@PathVariable UUID eventId) {

        eventService.removeEvent(eventId);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @Operation(summary = "Partially update event of an authorized user")
    @PatchMapping("{eventId}")
    public ResponseEntity<?> partiallyUpdateEvent(@PathVariable UUID eventId, @RequestBody @Valid EventUpdateRequestDto eventUpdateRequestDto) {

        return new RestResponse(
                HttpStatus.OK,
                eventService.partiallyUpdateEvent(eventId, eventUpdateRequestDto)
        ).getResponseEntity();

    }

    @Operation(summary = "Completely update event of an authorized user")
    @PutMapping("{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable UUID eventId, @RequestBody @Valid EventUpdateRequestDto eventUpdateRequestDto) {

        return new RestResponse(
                HttpStatus.OK,
                eventService.updateEvent(eventId, eventUpdateRequestDto)
        ).getResponseEntity();

    }

    @Operation(summary = "Invite a user to an event with visibility type SELECTED_INDIVIDUALS")
    @PostMapping("{eventId}/invite")
    public ResponseEntity<Void> inviteUser(@PathVariable UUID eventId, @RequestBody @Valid UserRequestDto userRequestDto) {

        eventService.inviteUser(eventId, userRequestDto);

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @Operation(summary = "Get all available chats for the authorized user.")
    @GetMapping("shared")
    public ResponseEntity<?> getSharedEvents() {

        return new RestResponse(
                HttpStatus.OK,
                eventService.getSharedEvents()
        ).getResponseEntity();

    }
}
