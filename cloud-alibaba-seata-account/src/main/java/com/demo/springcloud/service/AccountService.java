package com.demo.springcloud.service;

import com.demo.springcloud.entities.Account;

public interface AccountService {
    int create(Account account);
    int divide(Long userId,Integer money);
    Account getById(Long id);
}
