package com.smart.local.services.repoistory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.local.services.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);
}