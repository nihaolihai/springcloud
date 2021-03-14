package com.demo.springcloud.dao;

import com.demo.springcloud.entities.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderDao {
    int create(Order order);
    Order getById(@Param("id") Long id);
    void update(@Param("status") Integer status,@Param("userId") Long userId);
}
