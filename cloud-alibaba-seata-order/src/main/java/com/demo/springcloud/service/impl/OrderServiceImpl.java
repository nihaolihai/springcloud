package com.demo.springcloud.service.impl;

import com.demo.springcloud.dao.OrderDao;
import com.demo.springcloud.entities.Order;
import com.demo.springcloud.service.OrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderDao orderDao;

    @Override
    public int create(Order order) {
        return orderDao.create(order);
    }

    @Override
    public Order getById(Long id) {
        return orderDao.getById(id);
    }
}
