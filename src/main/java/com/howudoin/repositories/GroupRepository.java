package com.howudoin.repositories;

import com.howudoin.models.Group;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GroupRepository extends MongoRepository<Group, String> {
    // Find all groups where a specific user is a member
    List<Group> findByMembersContaining(String userId);

    // Find all groups managed by a specific admin
    List<Group> findByAdminId(String adminId);
}
