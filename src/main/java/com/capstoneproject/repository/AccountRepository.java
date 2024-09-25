package com.capstoneproject.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.capstoneproject.model.Account;

public interface AccountRepository extends MongoRepository<Account, String> {
    List<Account> findByUserId(String userId); // Find accounts by user ID
}
