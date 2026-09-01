package com.aep.redeSaber.repositories;

import com.aep.redeSaber.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}