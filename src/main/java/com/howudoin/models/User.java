package com.howudoin.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Document(collection = "users")
public class User {
    @Id
    private ObjectId id;
    private String firstName;
    private String lastName;

    @Indexed(unique = true)
    private String email;
    private String password;

    private List<ObjectId> friends = new ArrayList<>(); // List of friend user IDs
    private List<ObjectId> friendRequests = new ArrayList<>(); // Pending friend request IDs

    private List<Message> messages = new ArrayList<>();

    private List<Group> groups = new ArrayList<>();

    // Constructors
    public User() {}

    public User(Optional<User> user){
        this.id = user.map(User::getId).orElse(null);
        this.firstName = user.get().getFirstName();
        this.lastName = user.get().getLastName();
        this.email = user.get().getEmail();
        this.password = user.get().getPassword();
        this.friends = user.get().getFriendRequests();
        this.friendRequests = user.get().getFriendRequests();
        this.groups = user.get().getGroups();
        this.messages = user.get().get_messages();

    }
    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }


    // Check if the user has a valid state
    public boolean isPresent() {
        return email != null && !email.isEmpty() &&
                password != null && !password.isEmpty();
    }

    // Getters and Setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<ObjectId> getFriends() {
        return friends;
    }

    public void addFriend(ObjectId friend) {
        if (!this.friends.contains(friend)) {
            this.friends.add(friend);
        }
    }

    public void removeFriend(ObjectId friend) {}

    public void setFriends(List<ObjectId> friends) {
        this.friends = friends;
    }

    public List<ObjectId> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(List<ObjectId> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public void addFriendRequest(ObjectId friendRequest) {
        if (!this.friendRequests.contains(friendRequest)) {
            this.friendRequests.add(friendRequest);
        }
    }

    public void removeFriendRequest(ObjectId friendRequest) {
        System.out.println("Before removing friend request: " + this.friendRequests);
        this.friendRequests.removeIf(id -> id.equals(friendRequest));
        System.out.println("After removing friend request: " + this.friendRequests);
    }


    public List<Group> getGroups() {
        return this.groups;
    }

    public void addGroup(Group group) {
        this.groups.add(group);
    }

    public void changeGroup(Group group) {
        for(Group temp_group : this.groups) {
            if(temp_group.getId().equals(group.getId())) {
                int idx = this.groups.indexOf(temp_group);
                this.groups.set(idx, group);
            }
        }
    }

    public List<Message> get_messages() {
        return this.messages;
    }
    public void addPerson_message(Message message) {
        this.messages.add(message);
    }
    public void removePerson_message(Message message) {
        this.messages.remove(message);
    }


}
