package com.howudoin.services;

import com.howudoin.models.Group;
import com.howudoin.models.User;
import com.howudoin.repositories.GroupRepository;
import com.howudoin.repositories.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    // Create a new group
    public Group createGroup(String groupName, String adminId, List<String> memberIds) {
        ObjectId adminID = new ObjectId(adminId);


        Optional<User> admin = userRepository.findById(adminID);
        if (admin.isPresent()) {
            // Ensure the members list is mutable
            List<String> members = new ArrayList<>(memberIds);
            members.add(adminId); // Add the admin to the members list
            Group group = new Group(groupName, adminId, members);
            return groupRepository.save(group);
        }
        return null;
    }

    // Add a member to an existing group
    public boolean addMemberToGroup(String groupId, String memberId) {
        Optional<Group> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();
            if (!group.getMembers().contains(memberId)) {
                // Ensure the list is mutable before modifying
                group.getMembers().add(memberId);
                groupRepository.save(group);
                return true;
            }
        }
        return false;
    }

    // Retrieve all groups a user belongs to
    public List<Group> getUserGroups(String userId) {
        return groupRepository.findByMembersContaining(userId);
    }

    // Retrieve group members
    public List<User> getGroupMembers(String groupId) {
        Optional<Group> groupOpt = groupRepository.findById(groupId);

        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();
            return userRepository.findAllByIdIn(group.getMembers());
        }
        return null;
    }

    // Retrieve group message history
    public List<String> getGroupMessages(String groupId) {
        Optional<Group> groupOpt = groupRepository.findById(groupId);
        return groupOpt.map(Group::getMessages).orElse(null);
    }
}
