package com.howudoin.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "messages")
public class Message {
    @Id
    private String id;
    private String senderId; // ID of the user who sent the message
    private String recipientId; // Can be a user ID (private message) or group ID
    private String content;
    private String groupId = null;// exists if the message is for a group
    private LocalDateTime timestamp;

    // Constructors
    public Message() {}

    public Message(String senderId, String recipientId, String content, String isGroupMessage, LocalDateTime timestamp) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.content = content;
        this.groupId = isGroupMessage;
        this.timestamp = timestamp;
    }

    public Message(String senderId, String content, String isGroupMessage, LocalDateTime timestamp) {
        this.senderId = senderId;
        this.content = content;
        this.groupId = isGroupMessage;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String isGroupMessage() {
        return this.groupId;
    }

    public void setGroupMessage(String groupMessage) {
        this.groupId = groupMessage;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
