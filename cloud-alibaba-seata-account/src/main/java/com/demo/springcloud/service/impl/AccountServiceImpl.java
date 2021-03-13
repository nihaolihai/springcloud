package com.demo.springcloud.service.impl;

import com.demo.springcloud.dao.AccountDao;
import com.demo.springcloud.entities.Account;
import com.demo.springcloud.service.AccountService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AccountServiceImpl implements AccountService {

    @Resource
    private AccountDao accountDao;

    @Override
    public int create(Account account) {
        return accountDao.create(account);
    }

    @Override
    public Account getById(Long id) {
        return accountDao.getById(id);
    }
}
