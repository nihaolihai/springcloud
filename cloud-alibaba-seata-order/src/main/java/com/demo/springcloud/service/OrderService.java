package com.demo.springcloud.service;

import com.demo.springcloud.entities.Order;

public interface OrderService {
    int create(Order order);
    Order getById(Long id);
}
