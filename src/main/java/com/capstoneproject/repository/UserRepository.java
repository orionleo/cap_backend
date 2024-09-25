package com.capstoneproject.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.capstoneproject.model.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
}
