package com.howudoin.services;

import com.howudoin.models.User;
import com.howudoin.repositories.UserRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    // Test: Register a new user
    @Test
    public void testRegisterUser() {
        User user = new User("John", "Doe", "john@example.com", "password");
        when(userRepository.save(user)).thenReturn(user);

        User createdUser = userService.registerUser(user);

        assertNotNull(createdUser);
        assertEquals("John", createdUser.getFirstName());
    }

    // Test: Find user by email
    @Test
    public void testFindUserByEmail() {
        User user = new User("John", "Doe", "john@example.com", "password");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findUserByEmail("john@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("John", foundUser.get().getFirstName());
    }

    // Test: Get friends
    @Test
    public void testGetFriends() {
        User friend1 = new User("Alice", "Smith", "alice@example.com", "password");
        User friend2 = new User("Bob", "Brown", "bob@example.com", "password");
        when(userRepository.findAllByIdIn(Arrays.asList("friend1Id", "friend2Id")))
                .thenReturn(Arrays.asList(friend1, friend2));

        List<User> friends = userService.getFriends(Arrays.asList("friend1Id", "friend2Id"));

        assertEquals(2, friends.size());
        assertEquals("Alice", friends.get(0).getFirstName());
    }

    // Test: Send friend request
    @Test
    public void testSendFriendRequest() {
        User fromUser = new User("Alice", "Smith", "alice@example.com", "password");
        User toUser = new User("Bob", "Brown", "bob@example.com", "password");
        when(userRepository.findById(new ObjectId("adminId"))).thenReturn(Optional.of(fromUser));
        when(userRepository.findById(new ObjectId("adminId"))).thenReturn(Optional.of(toUser));
        when(userRepository.save(toUser)).thenReturn(toUser);

        boolean success = userService.sendFriendRequest("fromUserId", "toUserId");

        assertTrue(success);
        assertTrue(toUser.getFriendRequests().contains("fromUserId"));
    }

    // Test: Accept friend request
    @Test
    public void testAcceptFriendRequest() {
        User user = new User("Alice", "Smith", "alice@example.com", "password");
        User friend = new User("Bob", "Brown", "bob@example.com", "password");
        user.getFriendRequests().add("friendId");

        when(userRepository.findById(new ObjectId("adminId"))).thenReturn(Optional.of(user));
        when(userRepository.findById(new ObjectId("adminId"))).thenReturn(Optional.of(friend));
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.save(friend)).thenReturn(friend);

        boolean success = userService.acceptFriendRequest("userId", "friendId");

        assertTrue(success);
        assertTrue(user.getFriends().contains("friendId"));
        assertTrue(friend.getFriends().contains("userId"));
    }
}
