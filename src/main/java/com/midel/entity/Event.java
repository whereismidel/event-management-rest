package com.midel.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.midel.entity.enums.EventVisibility;
import com.midel.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Setter(AccessLevel.NONE)
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private EventVisibility visibility;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Setter(AccessLevel.NONE)
    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "expiration_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime expirationAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    @JsonManagedReference
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "events_allowed_users",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "allowed_users_id"))
    @Builder.Default
    private Set<User> allowedUsers = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allowed_chat_id")
    private Chat allowedChat;

    /**
     * Returns the set of users who are allowed to view the event based on its visibility setting.
     *
     * <p>
     * The visibility setting determines which users are permitted to access the event:
     * <ul>
     *   <li><strong>PUBLIC</strong>: Accessible to all users (returns <code>null</code>).</li>
     *   <li><strong>FRIENDS_ONLY</strong>: Accessible only to the owner's friends.</li>
     *   <li><strong>SELECTED_INDIVIDUALS</strong>: Accessible only to specifically allowed users.</li>
     *   <li><strong>SPECIFIC_CHAT</strong>: Accessible only to users in specific chats.</li>
     * </ul>
     * </p>
     *
     * @return the set of allowed users based on the event's visibility setting, or <code>null</code> if the event is public.
     */
    public Set<User> getSharedUsers() {
        return switch (visibility) {
            case PUBLIC -> null;
            case FRIENDS_ONLY -> {
                Set<User> shared = new HashSet<>(owner.getFriends());
                shared.add(owner);
                yield shared;
            }
            case SELECTED_INDIVIDUALS -> {
                Set<User> shared = new HashSet<>(allowedUsers);
                shared.add(owner);
                yield shared;
            }
            case SPECIFIC_CHAT -> allowedChat.getMembers();
        };
    }

    /**
     * Adds a user to the set of allowed users for the event.
     *
     * <p>
     * This method can only be called if the event's visibility is set to {@link EventVisibility#SELECTED_INDIVIDUALS}.
     * If the visibility is not set to {@link EventVisibility#SELECTED_INDIVIDUALS}, this method will throw an {@link IllegalStateException}.
     * </p>
     *
     * @param user the user to be added to the set of allowed users.
     * @throws IllegalArgumentException if the event visibility is not {@link EventVisibility#SELECTED_INDIVIDUALS}.
     */
    public void addAllowedUser(User user) {
        if (!visibility.equals(EventVisibility.SELECTED_INDIVIDUALS)) {
            throw new IllegalArgumentException("Cannot add allowed users unless the event visibility is set to SELECTED_INDIVIDUALS.");
        }

        if (allowedUsers == null) {
            allowedUsers = new LinkedHashSet<>();
        }

        if (allowedUsers.contains(user)) {
            throw new EntityNotFoundException("The user with the id=" + user.getId() + " is already invited");
        }

        allowedUsers.add(user);
    }

    /**
     * Adds a chat to the set of allowed chats for the event.
     *
     * <p>
     * This method can only be called if the event's visibility is set to {@link EventVisibility#SPECIFIC_CHAT}.
     * If the visibility is not set to {@link EventVisibility#SPECIFIC_CHAT}, this method will throw an {@link IllegalStateException}.
     * </p>
     *
     * @param chat the chat to be added to the set of allowed chats.
     * @throws IllegalArgumentException if the event visibility is not {@link EventVisibility#SPECIFIC_CHAT}.
     */
    public void setAllowedChat(Chat chat) {
        if (!visibility.equals(EventVisibility.SPECIFIC_CHAT)) {
            throw new IllegalArgumentException("Cannot set allowed chats unless the event visibility is set to SPECIFIC_CHAT.");
        }

        allowedChat = chat;
    }

    /**
     * Changes the visibility of the event and clears the allowed users and chats.
     *
     * <p>
     * This method updates the event's visibility to the specified value. If the new visibility is
     * {@link EventVisibility#SELECTED_INDIVIDUALS}, {@link EventVisibility#PUBLIC}, or {@link EventVisibility#FRIENDS_ONLY},
     * the method clears both the allowed users and allowed chats sets.
     * </p>
     *
     * <p>
     * If the specified visibility is the same as the current visibility, an {@link IllegalArgumentException} is thrown.
     * </p>
     *
     * @param eventVisibility the new visibility for the event
     * @throws IllegalArgumentException if the specified visibility is the same as the current visibility
     */
    public void changeVisibility(EventVisibility eventVisibility) {

        if (eventVisibility.equals(visibility)) {
            throw new IllegalArgumentException("The new visibility must be different from the current visibility.");
        }

        allowedUsers.clear();
        allowedChat = null;

        this.visibility = eventVisibility;
    }
}
