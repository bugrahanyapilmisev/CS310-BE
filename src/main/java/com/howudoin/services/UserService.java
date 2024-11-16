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
    public User findUserByEmail(String email) {
        return new User(userRepository.findByEmail(email));
    }
    public Optional<User> findUserById(ObjectId id) {
        return userRepository.findById(id);
    }
    public List<User> getFriendRequests(User user) {
        List<ObjectId> friendRequestsIDS = new ArrayList<>();
        for (ObjectId friendRequestId : user.getFriendRequests()) {
            friendRequestsIDS.add(friendRequestId);
        }
        return userRepository.findAllById(friendRequestsIDS);
    }


    // Retrieve a user's friends
    public List<ObjectId> getFriends(User user) {
        return user.getFriends();
    }


    // Retrieve friend requests sent to the user
    public List<User> getFriendRequests(String userId) {
        return userRepository.findByFriendRequestsContaining(userId);
    }

    // Send a friend request
    public boolean sendFriendRequest(User user, String toUserId) {
        ObjectId toId = new ObjectId(toUserId);
        Optional<User> toUserOpt = userRepository.findById(toId);

        if (toUserOpt.isPresent()) {
            User toUser = toUserOpt.get();
            if (!toUser.getFriendRequests().contains(user.getId())) {
                toUser.addFriendRequest(user.getId());
                userRepository.save(toUser); // Save only the receiver's friend request
                return true;
            }
        }
        return false;
    }


    // Accept a friend request
    public boolean acceptFriendRequest(User user, String friendId) {
        ObjectId fromId = new ObjectId(friendId);
        Optional<User> fromUserOpt = userRepository.findById(fromId);

        if (fromUserOpt.isPresent()) {
            User friend = fromUserOpt.get();

            if (user.getFriendRequests().contains(friend.getId())) {

                user.addFriend(friend.getId());
                friend.addFriend(user.getId());

                userRepository.save(user);
                userRepository.save(friend);
                user = userRepository.findById(user.getId()).orElseThrow(() -> new IllegalArgumentException("User not found after save"));

                user.removeFriendRequest(friend.getId());
                userRepository.save(user);


                return true;
            }
        }
        return false;
    }




    public boolean rejectFriendRequest(User user, String toUserId) {
        Optional<User> toUserOpt = userRepository.findById(new ObjectId(toUserId));
        if (toUserOpt.isPresent()) {
            User toUser = toUserOpt.get();
            // Remove the friend request if it exists
            if (user.getFriendRequests().contains(toUser.getId())) {
                user.removeFriendRequest(toUser.getId());
                userRepository.save(toUser);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    public User getUser(ObjectId id){
        return userRepository.findById(id).get();
    }



    public void save(User user){
        userRepository.save(user);
    }

}
