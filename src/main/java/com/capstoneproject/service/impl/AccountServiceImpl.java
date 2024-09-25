package com.capstoneproject.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capstoneproject.model.Account;
import com.capstoneproject.model.User;
import com.capstoneproject.repository.AccountRepository;
import com.capstoneproject.service.AccountService;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<Account> getAccountsByUser(String userId) {
        return accountRepository.findByUserId(userId); // Retrieve all accounts associated with a user
    }

    @Override
    public Optional<Account> getAccountById(String accountId) {
        return accountRepository.findById(accountId);
    }

}
