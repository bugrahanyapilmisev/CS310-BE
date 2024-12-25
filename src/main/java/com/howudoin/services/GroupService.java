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
    public boolean addMemberToGroup(Group group, String memberEmail) {
        if (group.isPresent()) {
            User new_user = userService.findUserByEmail(memberEmail);
            if (!group.getMembers().contains(new_user.getId())) {
                ObjectId group_id = group.getId();

                // Update the group's members list
                group.getMembers().add(new_user.getId());
                groupRepository.save(group);

                // Reload the User object to ensure it contains all current data
                new_user = userService.getUser(new_user.getId());

                // Add the group to the user's groups list without overwriting the field
                if (!new_user.getGroups().contains(group)) {
                    new_user.getGroups().add(group);
                }

                // Save the updated user back to the database
                userService.save(new_user);

                // Ensure all group members are updated with the group changes
                for (ObjectId memberId : group.getMembers()) {
                    User member = userService.getUser(memberId);
                    member.changeGroup(group); // Update group information in the member
                    userService.save(member);
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
        List<ObjectId> members = groupOpt.get().getMembers();
        if(!members.contains(user_sender.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are not member of this group");
        }
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
            return new ResponseEntity<>(Map.of("success", message,"sender mail",user_sender.getEmail()), HttpStatus.OK);

        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This group does not exist");
    }



    // Retrieve group members
    public List<Map<String,String>> getGroupMembers(ObjectId groupId) {
        Optional<Group> groupOpt = groupRepository.findById(groupId);

        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();
            List<ObjectId> members = group.getMembers();
            List<Map<String,String>> users = new ArrayList<>();

            for(ObjectId member : members) {
                Map<String,String> map = new HashMap<>();
                User user = userService.getUser(member);
                map.put("Email",user.getEmail());
                map.put("First Name",user.getFirstName());
                map.put("Last Name",user.getLastName());
                users.add(map);
            }
            return users;
        }
        return null;
    }

    // Retrieve group message history
    public List<Map<String, Object>> getGroupMessages(String groupId) {
        Optional<Group> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isPresent()) {
            List<Map<String, Object>> messages = new ArrayList<>();
            List<Message> groupMessages = groupOpt.get().getMessages();
            for (Message message : groupMessages) {
                String senderEmail = userService.findUserById(new ObjectId(message.getSenderId()))
                        .map(User::getEmail)
                        .orElse("Unknown Sender");

                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("id", message.getId());
                messageMap.put("senderId", message.getSenderId());
                messageMap.put("senderEmail", senderEmail);
                messageMap.put("content", message.getContent());
                messageMap.put("timestamp", message.getTimestamp().toString());

                messages.add(messageMap);
            }
            return messages;
        }
        return null;
    }

    public Group getGroup(ObjectId groupId) {
        return  groupRepository.findById(groupId).get();
    }

    public List<Map<String,String>> getGroups(String userid) {
        List<Group> groups = userService.findUserById(new ObjectId(userid)).get().getGroups();;
        List<Map<String,String>> groupinfos = new ArrayList<>();
        for(Group group : groups) {
            Map<String,String> map = new HashMap<>();
            map.put("name", group.getName());
            map.put("id",group.getId().toHexString());
            groupinfos.add(map);
        }
        return groupinfos;

    }
}
