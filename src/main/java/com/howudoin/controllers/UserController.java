package com.howudoin.controllers;


import com.howudoin.models.User;
import com.howudoin.security.JwtUtil;
import com.howudoin.security.LoginRequest;
import com.howudoin.security.TokenBlacklistService;
import com.howudoin.services.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;


    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inject PasswordEncoder

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName(); // Extract email from JWT
        User user = userService.findUserByEmail(email);
        if (user.isPresent()) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
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
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Extract the token
            long expirationTime = jwtUtil.extractExpiration(token).getTime(); // Extract expiration time
            tokenBlacklistService.addToBlacklist(token, expirationTime); // Add token to blacklist
            return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "No valid token provided."));
    }

    // Send a friend request
    @PostMapping("/friends/add")
    public ResponseEntity<String> sendFriendRequest(@RequestBody Map<String, String> payload) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findUserByEmail(email);
        user = userService.getUser(user.getId());
        String toUserId = payload.get("toUserId");
        if (user.getId().equals(new ObjectId(toUserId))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You can not add yourself as friend");
        } else {
            boolean success = userService.sendFriendRequest(user, toUserId);
            if (success) {
                return ResponseEntity.ok("Friend request sent successfully.");
            }
            return ResponseEntity.badRequest().body("Failed to send friend request.");
        }
    }


    @GetMapping("/friends/pending")
    public ResponseEntity<?> getPendingFriendRequests() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findUserByEmail(email);
        user = userService.getUser(user.getId());
        if (user.isPresent()) {
            // Fetch pending friend requests as User objects
            List<User> pendingRequests = userService.getFriendRequests(user);

            return ResponseEntity.ok(pendingRequests);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
    }

    @PostMapping("/friends/respond")
    public ResponseEntity<?> respondToFriendRequest(@RequestBody Map<String, String> payload) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User userOptional = userService.findUserByEmail(email);
        userOptional = userService.getUser(userOptional.getId());
        String toUserId = payload.get("toUserId");
        String response = payload.get("response");

        if (userOptional.getId().equals(new ObjectId(toUserId))) {
            return ResponseEntity.badRequest().body("You cannot add yourself as your friend.");
        }

        if ("accept".equals(response)) {
            boolean success = userService.acceptFriendRequest(userOptional, toUserId);
            if (success) {
                System.out.println("Friend request accepted successfully.");
                return ResponseEntity.ok("Friend request accepted.");
            }
        } else if ("reject".equals(response)) {
            boolean success = userService.rejectFriendRequest(userOptional, toUserId);
            if (success) {
                System.out.println("Friend request rejected successfully.");
                return ResponseEntity.ok("Friend request rejected.");
            }
        }
        return ResponseEntity.badRequest().body("Invalid request or operation failed.");
    }



    // Get the friend list
    @GetMapping("/friends")
    public ResponseEntity<?> getFriends() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findUserByEmail(email);
        user = userService.getUser(user.getId());
        if (user.isPresent()) {
            List<User> friends = new ArrayList<>();
            for(ObjectId i : userService.getFriends(user)){
                friends.add(userService.getUser(i));
            }
            return ResponseEntity.ok(friends);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
    }


}
