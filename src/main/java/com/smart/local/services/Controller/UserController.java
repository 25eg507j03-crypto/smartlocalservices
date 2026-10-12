
package com.smart.local.services.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smart.local.services.entity.User;
import com.smart.local.services.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private Map<String, Object> userResponse(User user) {
        Map<String, Object> response = new HashMap<>();

        response.put("id", user.getId());
        response.put("name", user.getName() == null ? "" : user.getName());
        response.put("email", user.getEmail() == null ? "" : user.getEmail());
        response.put("phone", user.getPhone() == null ? "" : user.getPhone());
        response.put("role", user.getRole() == null ? "" : user.getRole());

        return response;
    }

    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestBody User user, HttpSession session) {

        try {
            User savedUser = userService.registerUser(user);

            session.setAttribute("userId", savedUser.getId());
            session.setAttribute("userName", savedUser.getName());
            session.setAttribute("userEmail", savedUser.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(userResponse(savedUser));

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    // Login and store the actual user's ID in the session
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @RequestBody Map<String, String> credentials,
            HttpSession session) {

        String email = credentials.get("email");
        String password = credentials.get("password");

        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {

            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Email and password are required."));
        }

        Optional<User> authenticatedUser =
                userService.authenticateUser(email.trim(), password);

        if (authenticatedUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message",
                            "Invalid email or password."));
        }

        User user = authenticatedUser.get();

        session.setAttribute("userId", user.getId());
        session.setAttribute("userName", user.getName());
        session.setAttribute("userEmail", user.getEmail());

        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "Login successful.");
        resp.put("userId", user.getId());
        resp.put("userName", user.getName() != null ? user.getName() : "");
        resp.put("userEmail", user.getEmail() != null ? user.getEmail() : "");
        resp.put("role", user.getRole() != null ? user.getRole() : "");
        return ResponseEntity.ok(resp);
    }

    // Check the current login session
    @GetMapping("/session")
    public ResponseEntity<?> getSession(HttpSession session) {

        Object userId = session.getAttribute("userId");
        Object userEmail = session.getAttribute("userEmail");

        if (userId == null || userEmail == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please log in."));
        }

        Map<String, Object> details = new HashMap<>();

        details.put("userId", userId);
        details.put("userEmail", userEmail);

        Object userName = session.getAttribute("userName");

        if (userName != null) {
            details.put("userName", userName);
        }

        return ResponseEntity.ok(details);
    }

    // Logout
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpSession session) {
        session.invalidate();

        return ResponseEntity.ok(
                Map.of("message", "Logout successful."));
    }

    // Get all users
    @GetMapping
    public List<Map<String, Object>> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(this::userResponse)
                .toList();
    }

    // Get a user by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {

        Optional<User> user = userService.getUserById(id);

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userResponse(user.get()));
    }

    // Update a user
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {

        return userService.updateUser(id, user)
                .<ResponseEntity<?>>map(
                        updated -> ResponseEntity.ok(userResponse(updated)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete a user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        boolean deleted = userService.deleteUser(id);

        if (deleted) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    // Search a user by email
    @GetMapping("/search")
    public ResponseEntity<?> searchUserByEmail(
            @RequestParam String email) {

        return userService.searchUserByEmail(email)
                .<ResponseEntity<?>>map(
                        user -> ResponseEntity.ok(userResponse(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
