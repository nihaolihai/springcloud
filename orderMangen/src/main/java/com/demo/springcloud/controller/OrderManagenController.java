package com.demo.springcloud.controller;

import com.demo.springcloud.entities.CommoneResult;
import com.demo.springcloud.entities.Payment;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@RestController
public class OrderManagenController {

    public static final String PAY_URL = "http://localhost:8001";

    @Resource
    private RestTemplate restTemplate;

    @PostMapping(value = "/order/payment/create")
    public CommoneResult<Payment> create(Payment payment){
        return restTemplate.postForObject(PAY_URL+"payment/create",payment,CommoneResult.class);
    }

    @GetMapping(value = "/order/payment/get/{id}")
    public CommoneResult<Payment> get(@PathVariable("id") Long id){
        return restTemplate.getForObject(PAY_URL+"/payment/get/"+id,CommoneResult.class);
    }
}
