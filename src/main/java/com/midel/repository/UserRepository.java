package com.midel.repository;

import com.midel.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findUserById(Long id);

    boolean existsByUsername(String username);

}
