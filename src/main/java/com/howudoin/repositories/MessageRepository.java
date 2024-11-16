package com.howudoin.repositories;

import com.howudoin.models.Message;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends MongoRepository<Message, String> {
    // Find all messages sent between two users (private messaging)
    List<Message> findBySenderIdAndRecipientId(String senderId, String recipientId);

    // Find all messages in a group

    // Find all messages sent by a specific user
    List<Message> findBySenderId(String senderId);

    // Find all messages received by a specific user
    List<Message> findByRecipientId(String recipientId);

    Optional<Message> findById(ObjectId id);
}
