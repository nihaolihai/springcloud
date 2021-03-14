package com.demo.springcloud.dao;

import com.demo.springcloud.entities.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AccountDao {
    int create(Account account);
    int divide(@Param("userId") Long userId,@Param("money") Integer money);
    Account getById(@Param("id") Long id);
}
