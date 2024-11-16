/*package com.howudoin.services;

import com.howudoin.models.Group;
import com.howudoin.models.User;
import com.howudoin.repositories.GroupRepository;
import com.howudoin.repositories.UserRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
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
        ObjectId adminId = new ObjectId();
        User admin = new User("Alice", "Smith", "alice@example.com", "password");
        admin.setId(adminId);

        List<ObjectId> memberIds = new ArrayList<>(); // Use mutable ArrayList
        memberIds.add(new ObjectId());
        memberIds.add(new ObjectId());

        when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));

        Group group = new Group("Study Group", adminId, memberIds);
        when(groupRepository.save(any(Group.class))).thenReturn(group);

        // Act
        Group createdGroup = groupService.createGroup("Study Group", memberIds);

        // Assert
        assertNotNull(createdGroup, "Created group should not be null");
        assertEquals("Study Group", createdGroup.getName(), "Group name should match");
        assertTrue(createdGroup.getMembers().contains(adminId), "Admin should be a member of the group");

        // Verify save method is called
        verify(groupRepository, times(1)).save(any(Group.class));
    }



    // Test: Add member to a group
    @Test
    public void testAddMemberToGroup() {
        // Arrange
        ObjectId groupId = new ObjectId();
        ObjectId newMemberId = new ObjectId();

        Group group = new Group("Study Group", new ObjectId(), new ArrayList<>(List.of(new ObjectId())));
        when(groupRepository.findById(groupId.toHexString())).thenReturn(Optional.of(group)); // Return a mocked group
        when(groupRepository.save(any(Group.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        boolean success = groupService.addMemberToGroup(groupId, newMemberId.toHexString());

        // Assert
        assertTrue(success, "Member should be added successfully");
        assertTrue(group.getMembers().contains(newMemberId), "New member should be added to the group");
        verify(groupRepository, times(1)).save(group);
    }


}
*/