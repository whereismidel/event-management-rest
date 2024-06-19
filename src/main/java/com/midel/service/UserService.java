package com.midel.service;

import com.midel.dto.Mapper;
import com.midel.dto.user.UserResponseDto;
import com.midel.dto.user.UserWithFriendsResponseDto;
import com.midel.entity.User;
import com.midel.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User save(User user) {
        return userRepository.save(user);
    }

    public User create(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new EntityExistsException("A user with this username already exists");
        }

        return save(user);
    }

    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(("User with id=" + userId + " not found.")));
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User with username=" + username + " not found."));
    }

    public Collection<UserWithFriendsResponseDto> getAll() {
        return Mapper.INSTANCE.userToUserWithFriendsResponse(userRepository.findAll());
    }

    public Collection<UserResponseDto> getFriends() {
        User user = getCurrentUser();

        return Mapper.INSTANCE.userToUserResponse(user.getFriends());
    }

    public void addFriend(Long friendId) {
        User user = getCurrentUser();

        User friend = userRepository.findUserById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("A friend with this id doesn't exist."));

        if (!user.getFriends().add(friend)) {
            throw new EntityExistsException("You already have a friend with id=" + friendId);
        };

        userRepository.save(user);
    }


    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    /**
     * @return current user from the Spring Security context
     */
    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

    @Deprecated
    public void generateFriends() {
        List<User> userList = userRepository.findAll();

        for(User user : userRepository.findAll()) {
            int friendCount = new Random().nextInt(0, userList.size()-1);
            Collections.shuffle(userList);
            for (int i = 0; i < friendCount; i++) {
                if (!userList.get(i).getId().equals(user.getId())) {
                    user.getFriends().add(userList.get(i));
                }
            }
            userRepository.save(user);
        }
    }
}
