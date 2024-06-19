package com.midel.service;

import com.midel.dto.chat.ChatResponseDto;
import com.midel.dto.Mapper;
import com.midel.entity.Chat;
import com.midel.entity.User;
import com.midel.repository.ChatRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserService userService;

    public Collection<ChatResponseDto> getAll() {
        return Mapper.INSTANCE.chatToChatResponse(
                chatRepository.findAll()
        );
    }

    public ChatResponseDto create(String title) {

        User currentUser = userService.getCurrentUser();

        Set<User> members = new LinkedHashSet<>();
        members.add(currentUser);

        Chat chat = Chat.builder()
                .owner(currentUser)
                .title(title)
                .members(members)
                .build();

        chat = chatRepository.save(chat);

        return Mapper.INSTANCE.chatToChatResponse(chat);
    }

    public Collection<ChatResponseDto> getMy() {

        User currentUser = userService.getCurrentUser();

        return Mapper.INSTANCE.chatToChatResponse(currentUser.getChatsOwns());
    }

    public Chat getAuthUserChatById(UUID chatId) {

        User currentUser = userService.getCurrentUser();
        Chat chat = chatRepository.findById(chatId).orElse(null);

        if (chat == null || !chat.getOwner().getId().equals(currentUser.getId())) {
            throw new EntityNotFoundException("Chat with id=" + chatId + " not found.");
        }

        return chat;

    }

    public void addChatMember(UUID chatId, String username) {

        User userToAdd = userService.getByUsername(username);
        addChatMember(chatId, userToAdd);

    }

    public void addChatMember(UUID chatId, Long userId) {

        User userToAdd = userService.getById(userId);
        addChatMember(chatId, userToAdd);

    }

    private void addChatMember(UUID chatId, User userToAdd) {

        Chat chat = getAuthUserChatById(chatId);

        if (chat.getMembers().contains(userToAdd)) {
            throw new EntityNotFoundException("The user with the id=" + userToAdd.getId() + " is already in this chat");
        }

        chat.getMembers().add(userToAdd);

        chatRepository.save(chat);

    }

}
