package com.capstoneproject.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.capstoneproject.model.Transaction;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByFromAccountId(String fromAccountId);  // Find transactions by 'from' account ID
    List<Transaction> findByToAccountId(String toAccountId);      // Find transactions by 'to' account ID
    Optional<Transaction> findTopByOrderByTimestampDesc();// Find the latest transaction by timestamp
    
}
