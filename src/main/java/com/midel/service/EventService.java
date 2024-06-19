package com.midel.service;

import com.midel.dto.Mapper;
import com.midel.dto.event.EventCreateRequestDto;
import com.midel.dto.event.EventResponseDto;
import com.midel.dto.event.EventUpdateRequestDto;
import com.midel.dto.user.UserRequestDto;
import com.midel.entity.Chat;
import com.midel.entity.Event;
import com.midel.entity.User;
import com.midel.entity.enums.EventVisibility;
import com.midel.entity.enums.Role;
import com.midel.entity.enums.Status;
import com.midel.repository.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    
    private final EventRepository eventRepository;
    private final UserService userService;
    private final ChatService chatService;

    public Set<EventResponseDto> getAll() {

        return eventRepository.findAll()
                .stream()
                .map(this::getEventDtoFunction)
                .collect(Collectors.toSet());

    }

    public Set<EventResponseDto> getAllByStatus(Status status) {

        return eventRepository.queryEventsByStatus(status)
                .stream()
                .map(this::getEventDtoFunction)
                .collect(Collectors.toSet());

    }

    @Transactional
    public EventResponseDto createEvent(EventCreateRequestDto eventCreateRequestDto) {

        if (eventCreateRequestDto.getExpirationAt() != null && eventCreateRequestDto.getExpirationAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Expiration date must be in the future or null");
        }

        User currentUser = userService.getCurrentUser();
        Event event = Mapper.INSTANCE.eventCreateRequestToEvent(eventCreateRequestDto, currentUser);

        if (eventCreateRequestDto.getVisibility() == EventVisibility.SPECIFIC_CHAT) {
            if (eventCreateRequestDto.getAllowedChatId() == null) {
                throw new IllegalArgumentException("'allowedChatId' must be provided when visibility is SPECIFIC_CHAT");
            } else {
                Chat allowedChat = chatService.getAuthUserChatById(eventCreateRequestDto.getAllowedChatId());
                event.setAllowedChat(allowedChat);
            }
        }

        if (event.getVisibility().equals(EventVisibility.SELECTED_INDIVIDUALS)) {
            event.addAllowedUser(currentUser);
        }
        event = eventRepository.save(event);

        return getEventDtoFunction(event);
    }


    public Collection<EventResponseDto> getMyEvents() {

        User currentUser = userService.getCurrentUser();

        return currentUser.getCreatedEvents()
                .stream()
                .map(this::getEventDtoFunction)
                .collect(Collectors.toSet());
    }

    public Event getEvent(UUID eventId) {

        User currentUser = userService.getCurrentUser();

        if (!currentUser.getRole().equals(Role.ROLE_MODERATOR)) {
            throw new RuntimeException("Insufficient rights.");
        }

        Event event = eventRepository.findById(eventId).orElse(null);

        if (event == null) {
            throw new EntityNotFoundException("Event with id=" + eventId + " not found.");
        }

        return event;

    }

    public Event getAuthUserEventById(UUID eventId) {

        User currentUser = userService.getCurrentUser();
        Event event = eventRepository.findById(eventId).orElse(null);

        if (event == null
                || event.getOwner().getId().equals(currentUser.getId())
                && event.getStatus().equals(Status.REMOVED)
        ) {
            throw new EntityNotFoundException("Event with id=" + eventId + " not found.");
        }

        return event;

    }

    public void removeEvent(UUID eventId) {

        Event event = getAuthUserEventById(eventId);
        event.setStatus(Status.REMOVED);
        eventRepository.save(event);

    }

    @Transactional
    public EventResponseDto updateEvent(UUID eventId, EventUpdateRequestDto eventUpdateRequestDto) {

        Event event = getAuthUserEventById(eventId);
        Chat allowedChat = event.getAllowedChat();

        if (event.getAllowedChat() != null
                && eventUpdateRequestDto.getVisibility() == EventVisibility.SPECIFIC_CHAT
                && !event.getAllowedChat().getId().equals(eventUpdateRequestDto.getAllowedChatId())
        ) {
            if (eventUpdateRequestDto.getAllowedChatId() == null) {
                throw new IllegalArgumentException("'allowedChatId' must be provided when visibility is SPECIFIC_CHAT");
            } else {
                allowedChat = chatService.getAuthUserChatById(eventUpdateRequestDto.getAllowedChatId());
            }
        }

        event.setTitle(eventUpdateRequestDto.getTitle());
        event.setDescription(eventUpdateRequestDto.getDescription());

        if (!eventUpdateRequestDto.getVisibility().equals(event.getVisibility())) {
            event.changeVisibility(eventUpdateRequestDto.getVisibility());
        }

        event.setExpirationAt(eventUpdateRequestDto.getExpirationAt());

        if (event.getVisibility().equals(EventVisibility.SPECIFIC_CHAT)
                && eventUpdateRequestDto.getAllowedChatId() != null) {
            event.setAllowedChat(allowedChat);
        }

        eventRepository.save(event);

        return getEventDtoFunction(event);

    }

    @Transactional
    public EventResponseDto partiallyUpdateEvent(UUID eventId, EventUpdateRequestDto eventUpdateRequestDto) {

        Event event = getAuthUserEventById(eventId);
        Chat allowedChat = event.getAllowedChat();

        if (event.getAllowedChat() != null
                && eventUpdateRequestDto.getVisibility() == EventVisibility.SPECIFIC_CHAT
                && !event.getAllowedChat().getId().equals(eventUpdateRequestDto.getAllowedChatId())
        ) {
            if (eventUpdateRequestDto.getAllowedChatId() == null) {
                throw new IllegalArgumentException("'allowedChatId' must be provided when visibility is SPECIFIC_CHAT");
            } else {
                allowedChat = chatService.getAuthUserChatById(eventUpdateRequestDto.getAllowedChatId());
            }
        }

        if (eventUpdateRequestDto.getTitle() != null) {
            event.setTitle(eventUpdateRequestDto.getTitle());
        }
        if (eventUpdateRequestDto.getDescription() != null) {
            event.setDescription(eventUpdateRequestDto.getDescription());
        }
        if (eventUpdateRequestDto.getVisibility() != null
                && !eventUpdateRequestDto.getVisibility().equals(event.getVisibility())
        ) {
            event.changeVisibility(eventUpdateRequestDto.getVisibility());
        }
        if (eventUpdateRequestDto.getExpirationAt() != null) {
            event.setExpirationAt(eventUpdateRequestDto.getExpirationAt());
        }
        if (eventUpdateRequestDto.getAllowedChatId() != null && event.getVisibility().equals(EventVisibility.SPECIFIC_CHAT)) {
            event.setAllowedChat(allowedChat);
        }

        eventRepository.save(event);

        return getEventDtoFunction(event);

    }

    public void inviteUser(UUID eventId, UserRequestDto userRequestDto) {

        Event event = getAuthUserEventById(eventId);
        User user = userService.getById(userRequestDto.getUserId());

        event.addAllowedUser(user);
        eventRepository.save(event);
    }

    public Set<EventResponseDto> getSharedEvents() {

        User user = userService.getCurrentUser();

        return eventRepository.findAccessibleEvents(user.getId()).stream()
                .map(this::getEventDtoFunction)
                .collect(Collectors.toSet());
    }

    public void changeEventStatus(UUID eventId, Status status) {

        Event event = getEvent(eventId);

        if (event.getStatus().equals(status)) {
            throw new IllegalArgumentException("Current status match the provided status.");
        }

        event.setStatus(status);
        eventRepository.save(event);

    }

    public EventResponseDto getEventDtoFunction(Event event) {
        return switch (event.getVisibility()) {
            case PUBLIC -> Mapper.INSTANCE.eventToPublicEventResponse(event);
            case FRIENDS_ONLY -> Mapper.INSTANCE.eventToFriendsEventResponse(event);
            case SELECTED_INDIVIDUALS-> Mapper.INSTANCE.eventToIndividualEventResponse(event);
            case SPECIFIC_CHAT -> Mapper.INSTANCE.eventToChatEventResponse(event);
        };
    }
}
