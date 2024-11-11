package com.howudoin.controllers;

import com.howudoin.models.Message;
import com.howudoin.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // Send a message
    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(
            @RequestParam String senderId,
            @RequestParam String recipientId,
            @RequestBody String content,
            @RequestParam boolean isGroupMessage) {
        Message message = messageService.sendMessage(senderId, recipientId, content, isGroupMessage);
        return ResponseEntity.ok(message);
    }

    // Retrieve conversation history between two users
    @GetMapping("/conversation")
    public ResponseEntity<List<Message>> getConversation(
            @RequestParam String userId1,
            @RequestParam String userId2) {
        List<Message> messages = messageService.getConversation(userId1, userId2);
        return ResponseEntity.ok(messages);
    }

    // Retrieve messages sent to a group
    @GetMapping("/group")
    public ResponseEntity<List<Message>> getGroupMessages(@RequestParam String groupId) {
        List<Message> messages = messageService.getGroupMessages(groupId);
        return ResponseEntity.ok(messages);
    }
}
