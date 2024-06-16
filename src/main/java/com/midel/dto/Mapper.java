package com.midel.dto;

import com.midel.entity.Chat;
import com.midel.entity.User;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;

@org.mapstruct.Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface Mapper {

    Mapper INSTANCE = Mappers.getMapper(Mapper.class);

    UserWithFriendsDto userToUserWithFriendsDto(User user);
    Collection<UserWithFriendsDto> userCollectionsToUserWithFriendsDtoCollections(Collection<User> users);

    UserDto userToUserDto(User user);
    Collection<UserDto> userCollectionToUserDtoCollection(Collection<User> users);

    ChatDto chatToChatDto(Chat chat);
    Collection<ChatDto> chatCollectionToChatDtoCollection(Collection<Chat> chats);

}
