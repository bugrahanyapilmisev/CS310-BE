/*package com.howudoin.services;

import com.howudoin.models.Message;
import com.howudoin.repositories.MessageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MessageServiceTest {

    @Autowired
    private MessageService messageService;

    @MockBean
    private MessageRepository messageRepository;

    // Test: Send a message
    @Test
    public void testSendMessage() {
        // Arrange
        Message message = new Message("senderId", "recipientId", "Hello", false, LocalDateTime.now());
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        // Act
        Message sentMessage = messageService.sendMessage("senderId", "recipientId", "Hello", false);

        // Assert
        assertNotNull(sentMessage);
        assertEquals("Hello", sentMessage.getContent());
    }


    // Test: Get conversation history
    @Test
    public void testGetConversation() {
        Message message1 = new Message("senderId", "recipientId", "Hi", false, LocalDateTime.now());
        Message message2 = new Message("recipientId", "senderId", "Hello", false, LocalDateTime.now());
        when(messageRepository.findBySenderIdAndRecipientId("senderId", "recipientId"))
                .thenReturn(Arrays.asList(message1, message2));

        List<Message> conversation = messageService.getConversation("senderId", "recipientId");

        assertEquals(2, conversation.size());
        assertEquals("Hi", conversation.get(0).getContent());
    }
}*/
