package com.midel.service;

import com.midel.dto.event.*;
import com.midel.dto.user.UserRequestDto;
import com.midel.entity.*;
import com.midel.entity.enums.*;
import com.midel.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private EventService eventService;

    private User currentUser;
    private Event event;

    @BeforeEach
    void setUp() {
        // Mock currentUser
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("testuser");

        // Mock event
        event = new Event();
        event.setId(UUID.randomUUID());
        event.setTitle("Test Event");
        event.setDescription("Test Description");
        event.setOwner(currentUser);
        event.changeVisibility(EventVisibility.PUBLIC);
        event.setStatus(Status.OPENED);
    }

    @Test
    void testGetAll() {
        // Mock repository response
        when(eventRepository.findAll()).thenReturn(Collections.singletonList(event));

        Set<EventResponseDto> result = eventService.getAll();

        assertEquals(1, result.size());
        EventResponseDto dto = result.iterator().next();
        assertEquals(event.getId(), dto.getId());
        assertEquals(event.getTitle(), dto.getTitle());
        assertEquals(event.getDescription(), dto.getDescription());
        assertEquals(event.getOwner().getId(), dto.getOwner().getId());
        assertEquals(event.getVisibility(), dto.getVisibility());
    }

    @Test
    void testGetAllByStatus() {
        // Mock repository response
        when(eventRepository.queryEventsByStatus(Status.OPENED)).thenReturn(Collections.singletonList(event));

        Set<EventResponseDto> result = eventService.getAllByStatus(Status.OPENED);

        assertEquals(1, result.size());
        EventResponseDto dto = result.iterator().next();
        assertEquals(event.getId(), dto.getId());
        assertEquals(event.getTitle(), dto.getTitle());
        assertEquals(event.getDescription(), dto.getDescription());
        assertEquals(event.getOwner().getId(), dto.getOwner().getId());
        assertEquals(event.getVisibility(), dto.getVisibility());
    }

    @Test
    void testCreateEvent_PublicVisibility() {
        EventCreateRequestDto requestDto = new EventCreateRequestDto();
        requestDto.setTitle("New Event");
        requestDto.setDescription("New Event Description");
        requestDto.setVisibility(EventVisibility.PUBLIC);

        // Mock repository save
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        EventResponseDto result = eventService.createEvent(requestDto);

        assertNotNull(result);
        assertEquals(event.getId(), result.getId());
        assertEquals(event.getTitle(), result.getTitle());
        assertEquals(event.getDescription(), result.getDescription());
        assertEquals(event.getOwner().getId(), result.getOwner().getId());
        assertEquals(event.getVisibility(), result.getVisibility());
    }

    @Test
    void testCreateEvent_InvalidExpirationDate() {
        EventCreateRequestDto requestDto = new EventCreateRequestDto();
        requestDto.setTitle("New Event");
        requestDto.setDescription("New Event Description");
        requestDto.setVisibility(EventVisibility.PUBLIC);
        requestDto.setExpirationAt(LocalDateTime.now().minusDays(1)); // Set expiration date in the past

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> eventService.createEvent(requestDto));

        assertEquals("Expiration date must be in the future or null", exception.getMessage());
    }

    @Test
    void testGetMyEvents() {
        // Mock UserService to return a user
        when(userService.getCurrentUser()).thenReturn(currentUser);
        currentUser.setCreatedEvents(new LinkedHashSet<>());
        currentUser.setFriends(new LinkedHashSet<>());

        // Mock repository to return events
        Event event1 = new Event();
        event1.setId(UUID.randomUUID());
        event1.setTitle("Event 1");
        event1.setDescription("Description for Event 1");
        event1.changeVisibility(EventVisibility.PUBLIC);
        event1.setOwner(currentUser);

        Event event2 = new Event();
        event2.setId(UUID.randomUUID());
        event2.setTitle("Event 2");
        event2.setDescription("Description for Event 2");
        event2.changeVisibility(EventVisibility.FRIENDS_ONLY);
        event2.setOwner(currentUser);

        currentUser.getCreatedEvents().add(event1);
        currentUser.getCreatedEvents().add(event2);

        currentUser.getCreatedEvents().addAll(Set.of(event1, event2));

        Collection<EventResponseDto> result = eventService.getMyEvents();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(dto -> dto.getId().equals(event1.getId())));
        assertTrue(result.stream().anyMatch(dto -> dto.getId().equals(event2.getId())));
    }

    @Test
    void testGetEvent() {
        UUID eventId = UUID.randomUUID();
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("user1");
        currentUser.setRole(Role.ROLE_MODERATOR);

        Event event = new Event();
        event.setId(eventId);
        event.setTitle("Test Event");
        event.setDescription("Test Event Description");
        event.changeVisibility(EventVisibility.PUBLIC);
        event.setOwner(currentUser);

        // Mock UserService behavior
        when(userService.getCurrentUser()).thenReturn(currentUser);

        // Mock EventRepository behavior
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Call service method
        Event resultEvent = eventService.getEvent(eventId);

        // Assertions
        assertNotNull(resultEvent);
        assertEquals(event.getId(), resultEvent.getId());
        assertEquals(event.getTitle(), resultEvent.getTitle());
        assertEquals(event.getDescription(), resultEvent.getDescription());
        assertEquals(event.getOwner().getId(), resultEvent.getOwner().getId());
        assertEquals(event.getVisibility(), resultEvent.getVisibility());
    }

    @Test
    void testGetAuthUserEventById() {
        UUID eventId = UUID.randomUUID();
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("user1");
        currentUser.setRole(Role.ROLE_USER);

        Event event = new Event();
        event.setId(eventId);
        event.setTitle("Test Event");
        event.setDescription("Test Event Description");
        event.changeVisibility(EventVisibility.PUBLIC);
        event.setStatus(Status.OPENED);
        event.setOwner(currentUser);

        // Mock UserService behavior
        when(userService.getCurrentUser()).thenReturn(currentUser);

        // Mock EventRepository behavior
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Call service method
        Event resultEvent = eventService.getAuthUserEventById(eventId);

        // Assertions
        assertNotNull(resultEvent);
        assertEquals(event.getId(), resultEvent.getId());
        assertEquals(event.getTitle(), resultEvent.getTitle());
        assertEquals(event.getDescription(), resultEvent.getDescription());
        assertEquals(event.getOwner().getId(), resultEvent.getOwner().getId());
        assertEquals(event.getVisibility(), resultEvent.getVisibility());

        // Verify repository method call
        verify(eventRepository).findById(eventId);
    }

    @Test
    void testRemoveEvent() {
        UUID eventId = UUID.randomUUID();
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("user1");
        currentUser.setRole(Role.ROLE_USER);

        Event event = new Event();
        event.setId(eventId);
        event.setTitle("Test Event");
        event.setDescription("Test Event Description");
        event.changeVisibility(EventVisibility.PUBLIC);
        event.setStatus(Status.OPENED);
        event.setOwner(currentUser);

        // Mock UserService behavior
        when(userService.getCurrentUser()).thenReturn(currentUser);

        // Mock EventRepository behavior
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Call service method
        eventService.removeEvent(eventId);

        // Verify repository method call
        verify(eventRepository).findById(eventId);
        verify(eventRepository).save(event);
        assertEquals(Status.REMOVED, event.getStatus());
    }

    @Test
    void testUpdateEvent() {
        UUID eventId = UUID.randomUUID();
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("user1");
        currentUser.setRole(Role.ROLE_USER);
        currentUser.setFriends(new LinkedHashSet<>());

        Event existingEvent = new Event();
        existingEvent.setId(eventId);
        existingEvent.setTitle("Old Title");
        existingEvent.setDescription("Old Description");
        existingEvent.changeVisibility(EventVisibility.PUBLIC);
        existingEvent.setStatus(Status.OPENED);
        existingEvent.setOwner(currentUser);

        EventUpdateRequestDto updateRequestDto = new EventUpdateRequestDto();
        updateRequestDto.setTitle("New Title");
        updateRequestDto.setDescription("New Description");
        updateRequestDto.setVisibility(EventVisibility.FRIENDS_ONLY);

        // Mock UserService behavior
        when(userService.getCurrentUser()).thenReturn(currentUser);

        // Mock EventRepository behavior
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));

        // Call service method
        EventResponseDto updatedEventDto = eventService.updateEvent(eventId, updateRequestDto);

        // Assertions
        assertNotNull(updatedEventDto);
        assertEquals(existingEvent.getId(), updatedEventDto.getId());
        assertEquals(updateRequestDto.getTitle(), updatedEventDto.getTitle());
        assertEquals(updateRequestDto.getDescription(), updatedEventDto.getDescription());
        assertEquals(updateRequestDto.getVisibility(), updatedEventDto.getVisibility());

        // Verify repository method call
        verify(eventRepository).findById(eventId);
        verify(eventRepository).save(existingEvent);
    }

    @Test
    void testPartiallyUpdateEvent() {
        UUID eventId = UUID.randomUUID();
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("user1");
        currentUser.setRole(Role.ROLE_USER);

        Event existingEvent = new Event();
        existingEvent.setId(eventId);
        existingEvent.setTitle("Old Title");
        existingEvent.setDescription("Old Description");
        existingEvent.changeVisibility(EventVisibility.PUBLIC);
        existingEvent.setStatus(Status.OPENED);
        existingEvent.setOwner(currentUser);

        EventUpdateRequestDto updateRequestDto = new EventUpdateRequestDto();
        updateRequestDto.setTitle("New Title");

        // Mock UserService behavior
        when(userService.getCurrentUser()).thenReturn(currentUser);

        // Mock EventRepository behavior
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));

        // Call service method
        EventResponseDto updatedEventDto = eventService.partiallyUpdateEvent(eventId, updateRequestDto);

        // Assertions
        assertNotNull(updatedEventDto);
        assertEquals(existingEvent.getId(), updatedEventDto.getId());
        assertEquals(updateRequestDto.getTitle(), updatedEventDto.getTitle());
        assertEquals(existingEvent.getDescription(), updatedEventDto.getDescription()); // Description should remain unchanged

        // Verify repository method call
        verify(eventRepository).findById(eventId);
        verify(eventRepository).save(existingEvent);
    }

    @Test
    void testInviteUser() {
        UUID eventId = UUID.randomUUID();
        Long userId = 2L;

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("user1");
        currentUser.setRole(Role.ROLE_USER);

        User invitedUser = new User();
        invitedUser.setId(userId);
        invitedUser.setUsername("user2");
        invitedUser.setRole(Role.ROLE_USER);

        Event event = new Event();
        event.setId(eventId);
        event.setTitle("Test Event");
        event.setDescription("Test Event Description");
        event.changeVisibility(EventVisibility.SELECTED_INDIVIDUALS);
        event.setStatus(Status.OPENED);
        event.setOwner(currentUser);

        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUserId(userId);

        // Mock UserService behavior
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userService.getById(userId)).thenReturn(invitedUser);

        // Mock EventRepository behavior
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Call service method
        eventService.inviteUser(eventId, userRequestDto);

        // Assertions
        assertTrue(event.getSharedUsers().contains(invitedUser));

        // Verify repository method call
        verify(eventRepository).findById(eventId);
        verify(eventRepository).save(event);
    }

    @Test
    void testGetSharedEvents() {
        UUID eventId1 = UUID.randomUUID();
        UUID eventId2 = UUID.randomUUID();

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("user1");
        currentUser.setRole(Role.ROLE_USER);
        currentUser.setFriends(new LinkedHashSet<>());

        Event event1 = new Event();
        event1.setId(eventId1);
        event1.setTitle("Event 1");
        event1.setDescription("Event 1 Description");
        event1.setStatus(Status.OPENED);
        event1.changeVisibility(EventVisibility.PUBLIC);
        event1.setOwner(currentUser);

        Event event2 = new Event();
        event2.setId(eventId2);
        event2.setTitle("Event 2");
        event2.setDescription("Event 2 Description");
        event2.setStatus(Status.OPENED);
        event2.changeVisibility(EventVisibility.FRIENDS_ONLY);
        event2.setOwner(currentUser);

        List<Event> events = Arrays.asList(event1, event2);

        // Mock UserService behavior
        when(userService.getCurrentUser()).thenReturn(currentUser);

        // Mock EventRepository behavior
        when(eventRepository.findAccessibleEvents(currentUser.getId())).thenReturn(events);

        // Call service method
        Set<EventResponseDto> sharedEvents = eventService.getSharedEvents();

        // Assertions
        assertNotNull(sharedEvents);
        assertEquals(2, sharedEvents.size());

        // Verify repository method call
        verify(eventRepository).findAccessibleEvents(currentUser.getId());
    }

    @Test
    void testChangeEventStatus() {
        UUID eventId = UUID.randomUUID();
        Status newStatus = Status.REMOVED;

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("user1");
        currentUser.setRole(Role.ROLE_MODERATOR);

        Event event = new Event();
        event.setId(eventId);
        event.setTitle("Test Event");
        event.setDescription("Test Event Description");
        event.changeVisibility(EventVisibility.PUBLIC);
        event.setStatus(Status.OPENED);
        event.setOwner(currentUser);

        // Mock UserService behavior
        when(userService.getCurrentUser()).thenReturn(currentUser);

        // Mock EventRepository behavior
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Call service method
        assertDoesNotThrow(() -> eventService.changeEventStatus(eventId, newStatus));

        // Assertions
        assertEquals(newStatus, event.getStatus());

        // Verify EventRepository method call
        verify(eventRepository).findById(eventId);
        verify(eventRepository).save(event);
    }
}