package com.capstoneproject.service;

import java.math.BigDecimal;
import java.util.List;

import com.capstoneproject.model.Transaction;
import com.capstoneproject.model.Account;

public interface TransactionService {
    Transaction recordTransaction(Transaction transaction); // Record a new transaction
    List<Transaction> getMergedTransactionHistory(String accountId); // Retrieve transaction history for an account
    
    void transferMoney(Account fromAccount, Account toAccount, BigDecimal amount); // Transfer money
    String getLastTransactionId(); // Get the ID of the last transaction
}
