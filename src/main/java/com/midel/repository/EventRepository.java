package com.midel.repository;

import com.midel.entity.Event;
import com.midel.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    @Query("SELECT DISTINCT e FROM Event e " +
            "LEFT JOIN FETCH e.owner u " +
            "LEFT JOIN FETCH e.allowedUsers au " +
            "LEFT JOIN FETCH e.allowedChat c " +
            "LEFT JOIN u.friends uf " +
            "LEFT JOIN c.members cm " +
            "WHERE e.status = 'OPENED' " +
            "AND (" +
            "    e.visibility = 'PUBLIC' " +
            "    OR (e.visibility = 'FRIENDS_ONLY' AND uf.id = :userId) " +
            "    OR (e.visibility = 'SELECTED_INDIVIDUALS' AND au.id = :userId AND e.owner.id != :userId) " +
            "    OR (e.visibility = 'SPECIFIC_CHAT' AND cm.id = :userId AND e.owner.id != :userId) " +
            "    OR (e.visibility IN ('SELECTED_INDIVIDUALS', 'FRIENDS_ONLY') AND u.id = :userId)" +
            ")")
    List<Event> findAccessibleEvents(Long userId);

    List<Event> queryEventsByStatus(Status status);

}
