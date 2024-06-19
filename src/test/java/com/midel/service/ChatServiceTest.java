package com.midel.service;

import com.midel.dto.chat.ChatResponseDto;
import com.midel.entity.Chat;
import com.midel.entity.User;
import com.midel.repository.ChatRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ChatService chatService;

    private User currentUser;
    private Chat chat;

    @BeforeEach
    void setUp() {
        // Mock currentUser
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("testuser");
        currentUser.setPassword("123");
        currentUser.setChatsOwns(new LinkedHashSet<>());


        // Mock chat
        chat = new Chat();
        chat.setId(UUID.randomUUID());
        chat.setTitle("Test Chat");
        chat.setOwner(currentUser);
        chat.setMembers(new LinkedHashSet<>(Collections.singletonList(currentUser)));
    }

    @Test
    void testGetAll() {
        List<Chat> allChats = Collections.singletonList(chat);
        when(chatRepository.findAll()).thenReturn(allChats);

        Collection<ChatResponseDto> result = chatService.getAll();

        assertEquals(1, result.size());
        ChatResponseDto dto = result.iterator().next();
        assertEquals(chat.getId(), dto.getId());
        assertEquals(chat.getTitle(), dto.getTitle());
        assertEquals(chat.getOwner().getId(), dto.getOwner().getId());
    }

    @Test
    void testCreate() {
        String title = "New Chat";
        when(chatRepository.save(any(Chat.class))).thenReturn(chat);

        ChatResponseDto result = chatService.create(title);

        assertEquals(chat.getId(), result.getId());
        assertEquals(chat.getTitle(), result.getTitle());
        assertEquals(chat.getOwner().getId(), result.getOwner().getId());
        assertTrue(chat.getMembers().contains(currentUser));
    }

    @Test
    void testGetMy() {
        Set<Chat> currentUserChats = Collections.singleton(chat);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        currentUser.setChatsOwns(currentUserChats);

        Collection<ChatResponseDto> result = chatService.getMy();

        assertEquals(1, result.size());
        ChatResponseDto dto = result.iterator().next();
        assertEquals(chat.getId(), dto.getId());
        assertEquals(chat.getTitle(), dto.getTitle());
        assertEquals(chat.getOwner().getId(), dto.getOwner().getId());
    }

    @Test
    void testGetAuthUserChatById_Success() {
        UUID chatId = chat.getId();

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
        when(userService.getCurrentUser()).thenReturn(currentUser);

        Chat result = chatService.getAuthUserChatById(chatId);

        assertEquals(chat.getId(), result.getId());
        assertEquals(chat.getTitle(), result.getTitle());
        assertEquals(chat.getOwner(), result.getOwner());
    }

    @Test
    void testGetAuthUserChatById_NotFound() {
        UUID chatId = UUID.randomUUID();
        when(chatRepository.findById(chatId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> chatService.getAuthUserChatById(chatId));
    }

    @Test
    void testGetAuthUserChatById_Unauthorized() {
        UUID chatId = chat.getId();
        User unauthorizedUser = new User();
        unauthorizedUser.setId(2L);
        unauthorizedUser.setUsername("unauthorized");

        when(userService.getCurrentUser()).thenReturn(unauthorizedUser);
        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));

        assertThrows(EntityNotFoundException.class, () -> chatService.getAuthUserChatById(chatId));
    }

    @Test
    void testAddChatMember_Success() {
        Long userId = 2L;
        User userToAdd = new User();
        userToAdd.setId(userId);
        userToAdd.setUsername("newuser");

        when(chatRepository.findById(chat.getId())).thenReturn(Optional.of(chat));
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userService.getById(userId)).thenReturn(userToAdd);

        assertDoesNotThrow(() -> chatService.addChatMember(chat.getId(), userId));
        assertTrue(chat.getMembers().contains(userToAdd));
    }

    @Test
    void testAddChatMember_AlreadyExists() {
        Long userId = 1L;
        User userToAdd = new User();
        userToAdd.setId(userId);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        userToAdd.setUsername(currentUser.getUsername());

        when(chatRepository.findById(chat.getId())).thenReturn(Optional.of(chat));
        when(userService.getById(any(Long.class))).thenReturn(currentUser);

        // Add currentUser to chat.members
        chat.getMembers().add(currentUser);

        // Verify that EntityNotFoundException is thrown
        EntityExistsException exception = assertThrows(EntityExistsException.class,
                () -> chatService.addChatMember(chat.getId(), userId));

        assertEquals("The user with the id=" + userId + " is already in this chat", exception.getMessage());
    }
}