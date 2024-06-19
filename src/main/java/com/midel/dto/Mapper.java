package com.midel.dto;

import com.midel.dto.chat.ChatResponseDto;
import com.midel.dto.event.*;
import com.midel.dto.user.UserResponseDto;
import com.midel.dto.user.UserWithFriendsResponseDto;
import com.midel.entity.Chat;
import com.midel.entity.Event;
import com.midel.entity.User;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;

@org.mapstruct.Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface Mapper {

    Mapper INSTANCE = Mappers.getMapper(Mapper.class);

    UserWithFriendsResponseDto userToUserWithFriendsResponse(User user);
    Collection<UserWithFriendsResponseDto> userToUserWithFriendsResponse(Collection<User> users);

    UserResponseDto userToUserResponse(User user);
    Collection<UserResponseDto> userToUserResponse(Collection<User> users);

    ChatResponseDto chatToChatResponse(Chat chat);
    Collection<ChatResponseDto> chatToChatResponse(Collection<Chat> chats);

    @Mapping(target = "status", constant = "UNVERIFIED")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "expirationAt", defaultExpression = "java(null)")
    Event eventCreateRequestToEvent(EventCreateRequestDto eventCreateRequestDto);

    default Event eventCreateRequestToEvent(EventCreateRequestDto eventDto, User owner) {
        Event event = eventCreateRequestToEvent(eventDto);
        event.setOwner(owner);
        return event;
    }

    EventResponseDto eventToPublicEventResponse(Event event);

    @Mapping(target = "usersWithInvite", source = "event.sharedUsers")
    PrivateAccessEventResponseDto eventToFriendsEventResponse(Event event);

    @Mapping(target = "usersWithInvite", source = "event.sharedUsers")
    PrivateAccessEventResponseDto eventToIndividualEventResponse(Event event);

    @Mapping(target = "sharedChat", source = "event.allowedChat")
    ChatAccessEventResponseDto eventToChatEventResponse(Event event);
}
