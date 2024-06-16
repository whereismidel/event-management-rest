package com.midel.service;

import com.midel.dto.Mapper;
import com.midel.dto.UserWithFriendsDto;
import com.midel.entity.User;
import com.midel.exception.AlreadyExistException;
import com.midel.exception.NotFoundException;
import com.midel.repository.UserRepository;
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
            throw new AlreadyExistException("A user with this username already exists");
        }

        return save(user);
    }

    public Collection<UserWithFriendsDto> getAllUser() {
        return Mapper.INSTANCE.userCollectionsToUserWithFriendsDtoCollections(userRepository.findAll());
    }

    public Object getFriends() {
        User user = getCurrentUser();

        return Mapper.INSTANCE.userCollectionToUserDtoCollection(user.getFriends());
    }

    public void addFriend(Long friendId) {
        User user = getCurrentUser();

        User friend = userRepository.findUserById(friendId)
                .orElseThrow(() -> new NotFoundException("A friend with this id doesn't exist."));

        user.getFriends().add(friend);

        userRepository.save(user);
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public Optional<User> getById(Long userId) {
        return userRepository.findById(userId);
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

}
