package com.howudoin.services;

import com.howudoin.models.User;
import com.howudoin.repositories.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder
    @Autowired
    private final UserRepository userRepository;

    public UserService(PasswordEncoder passwordEncoder2, UserRepository userRepository2) {
        this.passwordEncoder = passwordEncoder2;
        this.userRepository = userRepository2;
    }
    // Register a new user
    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Hash the password
        return userRepository.save(user);
    }

    public Optional<User> loginUser(User user) {
        Optional<User> userOpt = userRepository.findByEmail(user.getEmail());
        if (userOpt.isPresent()) {
            User userFromDb = userOpt.get();
            // Validate the password
            if (passwordEncoder.matches(user.getPassword(), userFromDb.getPassword())) {
                return Optional.of(userFromDb); // Return the user if the login is successful
            }
            else{
                throw new IllegalArgumentException("Wrong password.");
            }
        }
        throw new IllegalArgumentException("Wrong Email"); // Return empty if login fails
    }

    // Find a user by their email
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Retrieve a user's friends
    public List<User> getFriends(List<String> friendIds) {
        return userRepository.findAllByIdIn(friendIds);
    }


    // Retrieve friend requests sent to the user
    public List<User> getFriendRequests(String userId) {
        return userRepository.findByFriendRequestsContaining(userId);
    }

    // Send a friend request
    public boolean sendFriendRequest(String fromUserId, String toUserId) {
        ObjectId fromId = new ObjectId(fromUserId);
        ObjectId toId = new ObjectId(toUserId);

        Optional<User> fromUserOpt = userRepository.findById(fromId);
        Optional<User> toUserOpt = userRepository.findById(toId);

        if (fromUserOpt.isPresent() && toUserOpt.isPresent()) {
            User toUser = toUserOpt.get();
            if (toUser.getFriendRequests() == null) {
                toUser.setFriendRequests(new ArrayList<>());
            }
            if (!toUser.getFriendRequests().contains(fromUserId)) {
                toUser.getFriendRequests().add(fromUserId);
                userRepository.save(toUser);
                return true;
            }
        }
        return false;
    }

    // Accept a friend request
    public boolean acceptFriendRequest(String userId, String friendId) {
        ObjectId fromId = new ObjectId(userId);
        ObjectId toId = new ObjectId(friendId);

        Optional<User> fromUserOpt = userRepository.findById(fromId);
        Optional<User> toUserOpt = userRepository.findById(toId);

        if (fromUserOpt.isPresent() && toUserOpt.isPresent()) {
            User user = fromUserOpt.get();
            User friend = toUserOpt.get();

            if (user.getFriendRequests().remove(friendId)) {
                user.getFriends().add(friendId);
                friend.getFriends().add(userId);

                userRepository.save(user);
                userRepository.save(friend);
                return true;
            }
        }
        return false;
    }
}
