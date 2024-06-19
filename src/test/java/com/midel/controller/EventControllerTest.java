package com.midel.controller;

import com.midel.dto.event.EventCreateRequestDto;
import com.midel.dto.event.EventResponseDto;
import com.midel.dto.event.EventUpdateRequestDto;
import com.midel.dto.user.UserRequestDto;
import com.midel.entity.Event;
import com.midel.entity.enums.EventVisibility;
import com.midel.response.RestResponse;
import com.midel.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testCreateEvent() {
        EventCreateRequestDto requestDto = new EventCreateRequestDto();
        requestDto.setTitle("Test Event");
        requestDto.setDescription("This is a test event");
        requestDto.setVisibility(EventVisibility.PUBLIC);
        requestDto.setExpirationAt(LocalDateTime.now().plusDays(7));

        EventResponseDto responseDto = new EventResponseDto();
        responseDto.setId(UUID.randomUUID());

        when(eventService.createEvent(any(EventCreateRequestDto.class))).thenReturn(responseDto);

        ResponseEntity<?> response = eventController.createEvent(requestDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(eventService).createEvent(requestDto);
    }

    @Test
    void testGetMyEvents() {
        Set<EventResponseDto> events = Set.of(new EventResponseDto());
        when(eventService.getMyEvents()).thenReturn(events);

        ResponseEntity<?> response = eventController.getMyEvents();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(events, ((RestResponse)response.getBody()).getData());
        verify(eventService).getMyEvents();
    }

    @Test
    void testGetEventById() {
        UUID eventId = UUID.randomUUID();
        Event event = new Event();
        event.changeVisibility(EventVisibility.PUBLIC);

        EventResponseDto responseDto = new EventResponseDto();
        when(eventService.getAuthUserEventById(eventId)).thenReturn(event);
        when(eventService.getEventDtoFunction(event)).thenReturn(responseDto);

        ResponseEntity<?> response = eventController.getEventById(eventId);
        assertEquals(HttpStatus.OK.value(), ((RestResponse)response.getBody()).getStatus());
        assertEquals(responseDto, ((RestResponse)response.getBody()).getData());
        verify(eventService).getAuthUserEventById(eventId);
    }

    @Test
    void testRemoveEvent() {
        UUID eventId = UUID.randomUUID();

        ResponseEntity<?> response = eventController.removeEvent(eventId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(eventService).removeEvent(eventId);
    }

    @Test
    void testPartiallyUpdateEvent() {
        UUID eventId = UUID.randomUUID();
        EventUpdateRequestDto requestDto = new EventUpdateRequestDto();
        EventResponseDto responseDto = new EventResponseDto();
        when(eventService.partiallyUpdateEvent(eq(eventId), any(EventUpdateRequestDto.class))).thenReturn(responseDto);

        ResponseEntity<?> response = eventController.partiallyUpdateEvent(eventId, requestDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, ((RestResponse)response.getBody()).getData());
        verify(eventService).partiallyUpdateEvent(eq(eventId), any(EventUpdateRequestDto.class));
    }

    @Test
    void testUpdateEvent() {
        UUID eventId = UUID.randomUUID();
        EventUpdateRequestDto requestDto = new EventUpdateRequestDto();
        EventResponseDto responseDto = new EventResponseDto();
        when(eventService.updateEvent(eq(eventId), any(EventUpdateRequestDto.class))).thenReturn(responseDto);

        ResponseEntity<?> response = eventController.updateEvent(eventId, requestDto);
        assertEquals(HttpStatus.OK.value(), ((RestResponse)response.getBody()).getStatus());
        assertEquals(responseDto, ((RestResponse)response.getBody()).getData());
        verify(eventService).updateEvent(eq(eventId), any(EventUpdateRequestDto.class));
    }

    @Test
    void testInviteUser() {
        UUID eventId = UUID.randomUUID();
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setUserId(1L);

        ResponseEntity<Void> response = eventController.inviteUser(eventId, requestDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(eventService).inviteUser(eventId, requestDto);
    }

    @Test
    void testGetSharedEvents() {
        Set<EventResponseDto> events = Set.of(new EventResponseDto());
        when(eventService.getSharedEvents()).thenReturn(events);

        ResponseEntity<?> response = eventController.getSharedEvents();
        assertEquals(HttpStatus.OK.value(), ((RestResponse)response.getBody()).getStatus());
        assertEquals(events, ((RestResponse)response.getBody()).getData());
        verify(eventService).getSharedEvents();
    }
}