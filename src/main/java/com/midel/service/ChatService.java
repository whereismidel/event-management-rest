package com.midel.service;

import com.midel.dto.ChatDto;
import com.midel.dto.Mapper;
import com.midel.entity.Chat;
import com.midel.entity.User;
import com.midel.exception.NotFoundException;
import com.midel.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserService userService;

    public Collection<ChatDto> getAll() {
        return Mapper.INSTANCE.chatCollectionToChatDtoCollection(
                chatRepository.findAll()
        );
    }

    public ChatDto create(String title) {

        User currentUser = userService.getCurrentUser();

        Set<User> members = new LinkedHashSet<>();
        members.add(currentUser);

        Chat chat = Chat.builder()
                .owner(currentUser)
                .title(title)
                .members(members)
                .build();

        chat = chatRepository.save(chat);

        currentUser.getChatsOwns().add(chat);
        currentUser.getMemberOfChats().add(chat);

        userService.save(currentUser);

        return Mapper.INSTANCE.chatToChatDto(chat);
    }

    public Collection<ChatDto> getMyChats() {

        User currentUser = userService.getCurrentUser();

        return Mapper.INSTANCE.chatCollectionToChatDtoCollection(currentUser.getChatsOwns());
    }

    public ChatDto getAuthUserChatById(UUID chatId) {

        User currentUser = userService.getCurrentUser();
        Optional<Chat> chat = chatRepository.findById(chatId);

        if (chat.isPresent() && currentUser.getChatsOwns().contains(chat.get())) {
            return Mapper.INSTANCE.chatToChatDto(chat.get());
        } else {
            throw new NotFoundException("Chat with id=" + chatId + " not found.");
        }

    }


    public void addChatMember(UUID chatId, Long userId) {

        User currentUser = userService.getCurrentUser();

        Optional<Chat> chat = chatRepository.findById(chatId);
        Optional<User> userToAdd = userService.getById(userId);

        if (chat.isEmpty() || !currentUser.getChatsOwns().contains(chat.get())) {
            throw new NotFoundException("Chat with id=" + chatId + " not found.");
        }

        if (userToAdd.isEmpty()) {
            throw new NotFoundException("User with id=" + userId + " not found.");
        }

        if (chat.get().getMembers().contains(userToAdd.get())) {
            throw new NotFoundException("The user with the id=" + userId + " is already in this chat");
        }

        chat.get().getMembers().add(userToAdd.get());

        chatRepository.save(chat.get());
    }

}
