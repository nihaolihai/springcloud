package com.demo.springcloud.service;

import com.demo.springcloud.entities.Account;

public interface AccountService {
    int create(Account account);
    Account getById(Long id);
}
