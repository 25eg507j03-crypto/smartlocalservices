
package com.smart.local.services.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.smart.local.services.entity.User;
import com.smart.local.services.repoistory.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Register a new user with a hashed password
    public User registerUser(User user) {
        if (userRepository.findByEmailIgnoreCase(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Check login credentials
    public boolean loginUser(String email, String password) {
        Optional<User> userOptional =
                userRepository.findByEmailIgnoreCase(email);

        if (userOptional.isEmpty()) {
            return false;
        }




        User user = userOptional.get();
        return passwordEncoder.matches(password, user.getPassword());
    }
    public Optional<User> authenticateUser(String email, String password) {
        Optional<User> userOptional =
                userRepository.findByEmailIgnoreCase(email);

        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();

        if (passwordEncoder.matches(password, user.getPassword())) {
            return Optional.of(user);
        }

        return Optional.empty();
    }


    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get a user by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Update a user
    public Optional<User> updateUser(Long id, User user) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());

            // Hash a new password only if one was provided
            if (user.getPassword() != null
                    && !user.getPassword().isBlank()) {
                existingUser.setPassword(
                        passwordEncoder.encode(user.getPassword()));
            }

            existingUser.setPhone(user.getPhone());
            existingUser.setRole(user.getRole());

            return userRepository.save(existingUser);
        });
    }

    // Delete a user
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }

        return false;
    }

    // Search for a user by email
    public Optional<User> searchUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }
}
