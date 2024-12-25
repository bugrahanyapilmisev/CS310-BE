package com.howudoin.services;

import com.howudoin.models.Group;
import com.howudoin.models.Message;
import com.howudoin.models.User;
import com.howudoin.repositories.MessageRepository;
import com.howudoin.repositories.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    // Send a message
    public Message sendMessage(String sender_id,String recipientId, String content, String isGroupMessage) {
        ObjectId objectId = new ObjectId(recipientId);
        User receiver = userService.getUser(objectId);
        User user = userService.getUser(new ObjectId(sender_id));
        Message message = new Message(sender_id,recipientId,content,isGroupMessage,LocalDateTime.now());

        messageRepository.save(message);
        message = messageRepository.findById(message.getId()).get();
        user.addPerson_message(message);
        receiver.addPerson_message(message);
        userRepository.save(receiver);
        userRepository.save(user);
        return messageRepository.save(message);
    }

    // Retrieve conversation history between two users
    public ResponseEntity<?> getConversation(User user) {
        Map<String,Object> map = new HashMap<>();
        map.put("User_Id", user.getId());
        map.put("User_name",user.getFirstName());
        map.put("User_LastName",user.getLastName());
        map.put("User_Email",user.getEmail());
        List<Message> messages_sent_inperson = new ArrayList<>();
        List<Message> messages_sent_ingroup = new ArrayList<>();
        List<Message> messages_received_inperson = new ArrayList<>();
        List<Message> messages_received_ingroup = new ArrayList<>();
        for(Message message : user.get_messages()) {
            if(message.getSenderId().equals(user.getId().toHexString())) {
                messages_sent_inperson.add(message);
            }
            else {
                messages_received_inperson.add(message);
            }
        }
        map.put("messages_sent_inperson", messages_sent_inperson);
        map.put("messages_received_inperson", messages_received_inperson);

       for(Group group : user.getGroups()) {
           for(Message message : group.getMessages() ) {
               if(message.getSenderId().equals(user.getId().toHexString())) {
                   messages_sent_ingroup.add(message);
               }
               else  {
                   messages_received_ingroup.add(message);
               }
           }
       }
        map.put("messages_sent_ingroup", messages_sent_ingroup);
        map.put("messages_received_ingroup", messages_received_ingroup);
        return ResponseEntity.ok(map);
    }

  public void save(Message message) {
        messageRepository.save(message);
  }


    public ResponseEntity<?> getspecialconversation(User user, String recipientId) {
        Map<String,Object> map = new HashMap<>();
        User friend = userService.getUser(new ObjectId(recipientId));
        map.put("User Name", user.getFirstName());
        map.put("Friend Name", friend.getLastName());
        List<Message> messages_sent_inperson = new ArrayList<>();
        List<Message> messages_received_inperson = new ArrayList<>();
        for(Message message : user.get_messages()) {
            if(message.getSenderId().equals(user.getId().toHexString()) && message.getRecipientId().equals(recipientId)) {
                messages_sent_inperson.add(message);
            }
            else if(message.getSenderId().equals(friend.getId().toHexString()) && message.getRecipientId().equals(user.getId().toHexString())) {
                messages_received_inperson.add(message);
            }
        }
        map.put("messages sent by user", messages_sent_inperson);
        map.put("messages sent by friend", messages_received_inperson);
        map.put("senderId",user.getId().toHexString());
        return ResponseEntity.ok(map);
    }
}
