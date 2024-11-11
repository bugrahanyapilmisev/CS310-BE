package com.howudoin.controllers;

import com.howudoin.models.Group;
import com.howudoin.models.User;
import com.howudoin.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    // Create a new group
    @PostMapping("/create")
    public ResponseEntity<Group> createGroup(
            @RequestParam String groupName,
            @RequestParam String adminId,
            @RequestBody List<String> memberIds) {
        Group group = groupService.createGroup(groupName, adminId, memberIds);
        if (group != null) {
            return ResponseEntity.ok(group);
        }
        return ResponseEntity.badRequest().body(null);
    }

    // Add a new member to an existing group
    @PostMapping("/{groupId}/add-member")
    public ResponseEntity<String> addMemberToGroup(@PathVariable String groupId, @RequestParam String memberId) {
        boolean success = groupService.addMemberToGroup(groupId, memberId);
        if (success) {
            return ResponseEntity.ok("Member added to group successfully.");
        }
        return ResponseEntity.badRequest().body("Failed to add member to group.");
    }

    // Retrieve all groups a user belongs to
    @GetMapping("/user")
    public ResponseEntity<List<Group>> getUserGroups(@RequestParam String userId) {
        List<Group> groups = groupService.getUserGroups(userId);
        return ResponseEntity.ok(groups);
    }

    // Retrieve group members
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<User>> getGroupMembers(@PathVariable String groupId) {
        List<User> members = groupService.getGroupMembers(groupId);
        return ResponseEntity.ok(members);
    }

    // Retrieve group message history
    @GetMapping("/{groupId}/messages")
    public ResponseEntity<List<String>> getGroupMessages(@PathVariable String groupId) {
        List<String> messages = groupService.getGroupMessages(groupId);
        return ResponseEntity.ok(messages);
    }
}
