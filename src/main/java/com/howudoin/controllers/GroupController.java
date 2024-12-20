package com.howudoin.controllers;

import com.howudoin.models.Group;
import com.howudoin.models.Message;
import com.howudoin.models.User;
import com.howudoin.services.GroupService;
import com.howudoin.services.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;


    // Create a new group
    @PostMapping("/create")
    public ResponseEntity<?> createGroup(@RequestBody Map<String, String> payload) {
        String groupName = payload.get("groupName");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findUserByEmail(email);
        user = userService.getUser(user.getId());
        if(payload.get("memberIDS").isEmpty()){
            return ResponseEntity.badRequest().body("You must provide memberIDs");
        }


        String[] memberIDS = payload.get("memberIDS").split("/");


        List<ObjectId> memberIds = new ArrayList<>();
        for (String memberID : memberIDS) {
            memberIds.add(new ObjectId(memberID));
        }

        memberIds.add(user.getId());
        Group group = groupService.createGroup(groupName, memberIds);
        if (group != null) {
            // Create the response map
            return ResponseEntity.ok(Map.of("Group has been created: ", group));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Group creation failed"));
    }

    // Add a new member to an existing group
    @PostMapping("/{group_id}/add-member")
    public ResponseEntity<?> addMemberToGroup(@PathVariable("group_id") String groupId, @RequestBody Map<String, String> payload) {
        ObjectId groupObjectId = new ObjectId(groupId);
        // Fetch the group using the provided ID
        Group group = groupService.getGroup(groupObjectId);
        if (group == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found.");
        }
        // Get the memberID from the payload
        String memberID = payload.get("new_memberID");
        if (memberID == null || memberID.isEmpty()) {
            return ResponseEntity.badRequest().body("Member ID must be provided.");
        }
        // Attempt to add the member to the group
        boolean success = groupService.addMemberToGroup(group, memberID);
        if (success) {
            return ResponseEntity.ok("Member added to group successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to add member to group.");
        }
    }

    // Add a new member to an existing group
    @PostMapping("/{group_id}/send")
    public ResponseEntity<?> sendMessagetoGroup(@PathVariable("group_id") String groupId, @RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        return groupService.sendMessage(groupId, message);
    }
    // Retrieve group members
    @GetMapping("/{group_id}/members")
    public ResponseEntity<List<User>> getGroupMembers(@PathVariable("group_id") String groupId) {

        List<User> members = groupService.getGroupMembers(new ObjectId(groupId));
        return ResponseEntity.ok(members);
    }

    // Retrieve group message history
    @GetMapping("/{groupId}/messages")
    public ResponseEntity<?> getGroupMessages(@PathVariable String groupId) {
        Group group = groupService.getGroup(new ObjectId(groupId));
        if (group == null) {
            return ResponseEntity.ok("The group with that group id is not found");
        }

        List<Message> messages = groupService.getGroupMessages(groupId);
        if(messages == null){
            if(messages.isEmpty()){
                return ResponseEntity.badRequest().body("No messages found.");
            }
            return ResponseEntity.badRequest().body(messages);
        }
        return ResponseEntity.ok(messages);


    }
}
