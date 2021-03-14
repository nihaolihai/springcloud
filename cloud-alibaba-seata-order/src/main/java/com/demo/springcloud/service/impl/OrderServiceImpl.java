package com.demo.springcloud.service.impl;

import com.demo.springcloud.dao.OrderDao;
import com.demo.springcloud.entities.Order;
import com.demo.springcloud.service.AccountService;
import com.demo.springcloud.service.OrderService;
import com.demo.springcloud.service.StoreService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderDao orderDao;
    @Resource
    private AccountService accountService;
    @Resource
    private StoreService storeService;

    @GlobalTransactional//全局事务控制
    public int create(Order order) {
        int result = orderDao.create(order);
        if(result>0){
            storeService.divide(order.getProductId(),order.getCount());
            accountService.divide(order.getUserId(),order.getMoney().intValue());
            orderDao.update(1,order.getUserId());
        }
        return result;
    }

    @Override
    public Order getById(Long id) {
        return orderDao.getById(id);
    }
}
