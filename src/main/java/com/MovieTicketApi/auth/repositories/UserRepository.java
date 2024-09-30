package com.MovieTicketApi.auth.repositories;

import com.MovieTicketApi.auth.entities.User;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String username);
}
