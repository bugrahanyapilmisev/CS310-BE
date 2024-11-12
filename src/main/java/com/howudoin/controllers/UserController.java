package com.howudoin.controllers;


import com.howudoin.models.User;
import com.howudoin.security.JwtUtil;
import com.howudoin.security.LoginRequest;
import com.howudoin.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inject PasswordEncoder

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName(); // Extract email from JWT
        Optional<User> user = userService.findUserByEmail(email);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    // Register a new user
    @GetMapping("/register/{username}/{password}")
    public ResponseEntity<?> registerUser(@PathVariable  String username, @PathVariable  String password) {
        return ResponseEntity.ok("object created with "+ username+" and "+ password);
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User createdUser = userService.registerUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // Handle global IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Optional<User> userOpt = userService.loginUser(new User(null, null, loginRequest.getEmail(), loginRequest.getPassword()));
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                String token = new JwtUtil().generateToken(user.getEmail()); // Generate JWT token
                return ResponseEntity.ok(Map.of(
                        "message", "Login successful",
                        "token", token
                ));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    // Send a friend request
    @PostMapping("/friends/add")
    public ResponseEntity<String> sendFriendRequest(@RequestBody Map<String, String> payload) {
        String fromUserId = payload.get("fromUserId");
        String toUserId = payload.get("toUserId");
        System.out.println(fromUserId);
        System.out.println(toUserId);
        boolean success = userService.sendFriendRequest(fromUserId, toUserId);
        if (success) {
            return ResponseEntity.ok("Friend request sent successfully.");
        }
        return ResponseEntity.badRequest().body("Failed to send friend request.");
    }

    // Accept a friend request
    @PostMapping("/friends/accept")
    public ResponseEntity<String> acceptFriendRequest(@RequestBody Map<String, String> payload) {
        String fromUserId = payload.get("fromUserId");
        String toUserId = payload.get("toUserId");
        boolean success = userService.acceptFriendRequest(fromUserId, toUserId);
        if (success) {
            return ResponseEntity.ok("Friend request accepted.");
        }
        return ResponseEntity.badRequest().body("Failed to accept friend request.");
    }

    // Get the friend list
    // Get the friend list
    @GetMapping("/friends")
    public ResponseEntity<List<User>> getFriends(@RequestParam String id) {
        // Fetch the user by ID
        Optional<User> userOptional = userService.findUserByEmail(id); // Replace with findById if available
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Use the friend's IDs to fetch the actual friend User objects
            List<User> friends = userService.getFriends(user.getFriends());
            return ResponseEntity.ok(friends);
        }
        return ResponseEntity.badRequest().body(null);
    }

}
