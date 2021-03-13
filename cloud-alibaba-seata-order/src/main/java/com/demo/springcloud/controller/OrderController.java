package com.demo.springcloud.controller;

import com.demo.springcloud.entities.CommoneResult;
import com.demo.springcloud.entities.Order;
import com.demo.springcloud.service.OrderService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class OrderController {

    @Resource
    private OrderService orderService;

    @PostMapping(value = "/seata/order/create")
    public CommoneResult create(@RequestBody Order order){
        int result = orderService.create(order);
        if(result>0){
            return new CommoneResult(200,"插入成功",result);
        }else{
            return new CommoneResult(666,"插入失败",null);
        }
    }

    @GetMapping(value = "/seata/order/get/{id}")
    public CommoneResult<Order> get(@PathVariable("id") Long id){
        Order result = orderService.getById(id);
        if(result!=null){
            return new CommoneResult(200,"查询成功,结果：",result);
        }else{
            return new CommoneResult(666,"查询失败，传入id："+id,null);
        }
    }

}
