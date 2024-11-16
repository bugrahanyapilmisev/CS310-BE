package com.howudoin.repositories;

import com.howudoin.models.Group;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends MongoRepository<Group, String> {
    // Find all groups where a specific user is a member
    List<Group> findByMembersContaining(ObjectId userId);

    // Find all groups managed by a specific admin

    Optional<Group> findById(ObjectId id);
}
