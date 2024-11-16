/*package com.howudoin.services;

import com.howudoin.models.User;
import com.howudoin.repositories.UserRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        // Arrange
        User user = new User("John", "Doe", "john@example.com", "password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User createdUser = userService.registerUser(user);

        // Assert
        assertNotNull(createdUser);
        assertEquals("John", createdUser.getFirstName());
        assertEquals("Doe", createdUser.getLastName());
        verify(userRepository, times(1)).save(user);
    }

    // Test: Find user by email
    @Test
    public void testFindUserByEmail() {
        // Arrange
        User user = new User("John", "Doe", "john@example.com", "password");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        // Act
        User foundUser = userService.findUserByEmail("john@example.com");

        // Assert
        assertNotNull(foundUser);
        assertEquals("John", foundUser.getFirstName());
    }

    // Test: Get friends
    @Test
    public void testGetFriends() {
        // Arrange
        ObjectId friend1Id = new ObjectId();
        ObjectId friend2Id = new ObjectId();

        User friend1 = new User("Alice", "Smith", "alice@example.com", "password");
        friend1.setId(friend1Id);
        User friend2 = new User("Bob", "Brown", "bob@example.com", "password");
        friend2.setId(friend2Id);

        when(userRepository.findAllByIdIn(List.of(friend1Id, friend2Id))).thenReturn(List.of(friend1, friend2));

        // Act
        List<User> friends = userService.getFriends(List.of(friend1Id, friend2Id));

        // Assert
        assertEquals(2, friends.size(), "Friends list size should match");
        assertEquals("Alice", friends.get(0).getFirstName(), "First friend's name should match");
        assertEquals("Bob", friends.get(1).getFirstName(), "Second friend's name should match");
    }


    // Test: Send friend request
    @Test
    public void testSendFriendRequest() {
        // Arrange
        ObjectId fromUserId = new ObjectId();
        ObjectId toUserId = new ObjectId();

        User fromUser = new User("Alice", "Smith", "alice@example.com", "password");
        fromUser.setId(fromUserId);
        User toUser = new User("Bob", "Brown", "bob@example.com", "password");
        toUser.setId(toUserId);

        when(userRepository.findById(fromUserId)).thenReturn(Optional.of(fromUser));
        when(userRepository.findById(toUserId)).thenReturn(Optional.of(toUser));

        // Act
        boolean success = userService.sendFriendRequest(new User(userRepository.findById(fromUserId)), toUserId.toHexString());

        // Assert
        assertTrue(success);
        assertTrue(toUser.getFriendRequests().contains(fromUserId));
        verify(userRepository, times(1)).save(toUser);
    }

    // Test: Accept friend request
    @Test
    public void testAcceptFriendRequest() {
        // Arrange
        ObjectId userId = new ObjectId();
        ObjectId friendId = new ObjectId();

        User user = new User("Alice", "Smith", "alice@example.com", "password");
        user.setId(userId);
        user.getFriendRequests().add(friendId);

        User friend = new User("Bob", "Brown", "bob@example.com", "password");
        friend.setId(friendId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(friendId)).thenReturn(Optional.of(friend));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Mock save

        // Act
        Optional<User> userOpt = userRepository.findById(userId);
        boolean success = userService.acceptFriendRequest(userOpt.get(), friendId.toHexString());

        // Assert
        assertTrue(success, "Friend request should be accepted");
        assertTrue(user.getFriends().contains(friendId), "Friend ID should be added to user's friend list");
        assertTrue(friend.getFriends().contains(userId), "User ID should be added to friend's friend list");

        // Verify save methods are called
        verify(userRepository, times(1)).save(user);
        verify(userRepository, times(1)).save(friend);
    }


}
*/