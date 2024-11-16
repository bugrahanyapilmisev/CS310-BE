package com.howudoin.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "groups")
public class Group {
    @Id
    private ObjectId id;
    private String name; // Group name
    private List<ObjectId> members = new ArrayList<>(); // List of user IDs who are members of the group
    private List<Message> messages = new ArrayList<>(); // List of message IDs associated with this group

    // Constructors
    public Group() {}

    public Group(String name, List<ObjectId> members2) {
        this.name = name;
        this.members.addAll(members2);
    }


    public boolean isPresent(){
        if(id != null && name != null ){
            return true;
        }
        return false;
    }

    // Getters and Setters
    public ObjectId getId() {
        return this.id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<ObjectId> getMembers() {
        return members;
    }

    public void addMember(ObjectId memberId) {
        members.add(memberId);
    }

    public void setMembers(List<ObjectId> members) {
        this.members = members;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addMessages(Message messages) {
        this.messages.add(messages);
    }
}
