package com.howudoin.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "groups")
public class Group {
    @Id
    private String id;
    private String name; // Group name
    private String adminId; // ID of the user who created the group
    private List<String> members = new ArrayList<>(); // List of user IDs who are members of the group
    private List<String> messages = new ArrayList<>(); // List of message IDs associated with this group

    // Constructors
    public Group() {}

    public Group(String name, String adminId, List<String> members) {
        this.name = name;
        this.adminId = adminId;
        this.members = members;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}
