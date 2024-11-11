package com.howudoin.services;

import com.howudoin.models.Group;
import com.howudoin.models.User;
import com.howudoin.repositories.GroupRepository;
import com.howudoin.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class GroupServiceTest {

    @Autowired
    private GroupService groupService;

    @MockBean
    private GroupRepository groupRepository;

    @MockBean
    private UserRepository userRepository;

    // Test: Create a group
    @Test
    public void testCreateGroup() {
        // Arrange
        User admin = new User("Alice", "Smith", "alice@example.com", "password");
        when(userRepository.findById("adminId")).thenReturn(Optional.of(admin));

        Group group = new Group("Study Group", "adminId", new ArrayList<>(List.of("member1", "member2", "adminId")));
        when(groupRepository.save(any(Group.class))).thenReturn(group);

        // Act
        Group createdGroup = groupService.createGroup("Study Group", "adminId", List.of("member1", "member2"));

        // Assert
        assertNotNull(createdGroup);
        assertEquals("Study Group", createdGroup.getName());
        assertTrue(createdGroup.getMembers().contains("adminId")); // Ensure admin is added to the group
    }


    // Test: Add member to a group
    @Test
    public void testAddMemberToGroup() {
        // Arrange
        Group group = new Group("Study Group", "adminId", new ArrayList<>(List.of("member1")));
        when(groupRepository.findById("groupId")).thenReturn(Optional.of(group));
        when(groupRepository.save(any(Group.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        boolean success = groupService.addMemberToGroup("groupId", "member2");

        // Assert
        assertTrue(success);
        assertTrue(group.getMembers().contains("member2")); // Ensure the member was added
    }

}
