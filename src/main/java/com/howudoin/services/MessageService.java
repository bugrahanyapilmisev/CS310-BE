package com.howudoin.services;

import com.howudoin.models.Message;
import com.howudoin.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    // Send a message
    public Message sendMessage(String senderId, String recipientId, String content, boolean isGroupMessage) {
        Message message = new Message(senderId, recipientId, content, isGroupMessage, LocalDateTime.now());
        return messageRepository.save(message);
    }

    // Retrieve conversation history between two users
    public List<Message> getConversation(String userId1, String userId2) {
        return messageRepository.findBySenderIdAndRecipientId(userId1, userId2);
    }

    // Retrieve messages sent to a group
    public List<Message> getGroupMessages(String groupId) {
        return messageRepository.findByRecipientIdAndIsGroupMessage(groupId, true);
    }
}
