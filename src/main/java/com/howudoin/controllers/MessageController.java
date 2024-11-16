package com.howudoin.controllers;

import com.howudoin.models.Message;
import com.howudoin.models.User;
import com.howudoin.services.MessageService;
import com.howudoin.services.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    // Send a message
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, String> payload) {
        String recipientId = payload.get("recipientId");
        String content = payload.get("content");
        String isGroupMessage = "";

        // Validate content and recipient
        if (content.isEmpty() || recipientId == null || recipientId.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User sender = userService.findUserByEmail(email);
        sender = userService.getUser(sender.getId());

        // Ensure sender and recipient are not the same
        if (sender.getId().toHexString().equals(recipientId)) {
            return ResponseEntity.badRequest().build();
        }

        if(!sender.getFriends().contains(new ObjectId(recipientId))) {
            return ResponseEntity.badRequest().body("You are not friend of this recipient");
        }

        // Send message
        Message message = messageService.sendMessage(sender.getId().toHexString(), recipientId, content, isGroupMessage);
        return ResponseEntity.ok(message);
    }



    @GetMapping("")
    public ResponseEntity<?> getAllMessages() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findUserByEmail(email);
        user = userService.getUser(user.getId());
        return messageService.getConversation(user);
    }
}
