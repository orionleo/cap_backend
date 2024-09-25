package com.capstoneproject.service;

import java.util.List;
import java.util.Optional;

import com.capstoneproject.model.Account;
import com.capstoneproject.model.User;

public interface AccountService {
    Account createAccount(User user, Account account);         // Create a new account for a user
    List<Account> getAccountsByUser(String userId);                // Retrieve all accounts for a user
    Optional<Account> getAccountById(String accountId);        // Retrieve account details by ID
    void transferFunds(String fromAccountId, String toAccountId, double amount); // Transfer funds between accounts

}
