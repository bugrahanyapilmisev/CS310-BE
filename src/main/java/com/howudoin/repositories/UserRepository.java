package com.howudoin.repositories;

import com.howudoin.models.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User,ObjectId> {
    // Find a user by their email
    Optional<User> findByEmail(String email);

    // Find all users by a list of user IDs (useful for fetching friends)
    List<User> findAllByIdIn(List<ObjectId> ids);

    // Find all users with a specific name (case-sensitive)
    List<User> findByFirstName(String firstName);

    // Find users who have pending friend requests from a specific user
    List<User> findByFriendRequestsContaining(String userId);

    Optional<User> findById(ObjectId id);
}
