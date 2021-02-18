package com.demo.springcloud.controller;

import com.demo.springcloud.service.PayManagenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
public class PayManagenController {

    @Resource
    private PayManagenService payManagenService;
    @Value("${server.port}")
    private  String port;

    @GetMapping(value = "/hystrix/paymentok/{id}")
    public String paymentInfoOK(@PathVariable("id")  Integer id){
        return payManagenService.paymentInfoOK(id);
    }

    @GetMapping(value = "/hystrix/paymenterr/{id}")
    public String paymentInfoErro(@PathVariable("id")  Integer id){
        return payManagenService.paymentInfoErro(id);
    }
}
