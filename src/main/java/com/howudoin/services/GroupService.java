package com.howudoin.services;

import com.howudoin.models.Group;
import com.howudoin.models.Message;
import com.howudoin.models.User;
import com.howudoin.repositories.GroupRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    // Create a new group
    public Group createGroup(String groupName, List<ObjectId> memberIds) {
        // Ensure the members list is mutable
        Group group = new Group(groupName, memberIds);
        for(ObjectId memberId : memberIds) {
            User user = userService.getUser(memberId);
            user.addGroup(group);
            groupRepository.save(group);
            userService.save(user);
            group = groupRepository.findById(group.getId()).get();
        }
        return groupRepository.save(group);
    }

    public Map<String, Object> showGroup(Group group) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("groupId", group.getId().toHexString());
        response.put("groupName", group.getName());
        // Add members;
        List<String> emails = new ArrayList<>();
        for (ObjectId memberId : group.getMembers()) {
            String email = userService.getUser(memberId).getEmail();
            emails.add(email);
        }
        response.put("emails", emails);
        return response;
    }

    // Handle global IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
    }
    // Add a member to an existing group
    public boolean addMemberToGroup(Group group, String memberId) {
        if (group.isPresent()) {
            User new_user = userService.getUser(new ObjectId(memberId));
            if (!group.getMembers().contains(new_user.getId())) {
                // Ensure the list is mutable before modifying
                group.getMembers().add(new_user.getId());
                new_user.getGroups().add(group);
                userService.save(new_user);
                groupRepository.save(group);
                for(ObjectId member : group.getMembers()) {
                    User user = userService.getUser(member);
                    user.changeGroup(group);
                    userService.save(user);
                }
                return true;
            }
        }
        return false;
    }

    public ResponseEntity<?> sendMessage(String groupid_str,String content) {
        ObjectId groupId = new ObjectId(groupid_str);
        Optional<Group> groupOpt = groupRepository.findById(groupId);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user_sender = userService.findUserByEmail(email);
        user_sender = userService.getUser(user_sender.getId());

        if(groupOpt.isPresent()) {
            Group group = groupOpt.get();
            List<ObjectId> memberIds = group.getMembers();
            String sender_id = user_sender.getId().toHexString();
            String receiver_id = "";
            Message message = new Message(sender_id,receiver_id,content,groupid_str,LocalDateTime.now());
            group.addMessages(message);
            groupRepository.save(group);
            group = groupRepository.findById(group.getId()).get();
            for(ObjectId memberId : memberIds) {
                User user = userService.getUser(memberId);
                user.changeGroup(group);
                userService.save(user);
            }
            messageService.save(message);
            return new ResponseEntity<>(Map.of("success", message), HttpStatus.OK);

        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This group does not exist");
    }



    // Retrieve group members
    public List<User> getGroupMembers(ObjectId groupId) {
        Optional<Group> groupOpt = groupRepository.findById(groupId);

        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();
            List<ObjectId> members = group.getMembers();
            List<User> users = new ArrayList<>();
            for(ObjectId member : members) {
                users.add(userService.getUser(member));
            }
            return users;
        }
        return null;
    }

    // Retrieve group message history
    public List<Message> getGroupMessages(String groupId) {
        Optional<Group> groupOpt = groupRepository.findById(groupId);
        if(groupOpt.isPresent()){
            return groupOpt.map(Group::getMessages).orElse(null);
        }
        return null;
    }

    public Group getGroup(ObjectId groupId) {
        return  groupRepository.findById(groupId).get();
    }
}
