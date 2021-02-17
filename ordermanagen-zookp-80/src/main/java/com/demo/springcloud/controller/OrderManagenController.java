package com.demo.springcloud.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * 服务调用
 */
@RestController
@Slf4j
public class OrderManagenController {
    @Value("${server.port}")
    private String serverPort;
    //单机调用
    public static final String PAY_URL = "http://cloud-provider-payment";

    @Resource
    private RestTemplate restTemplate;

    @GetMapping(value = "/consume/order/payment/zk")
    public String getPaymentInfo(){
        String result = restTemplate.getForObject(PAY_URL+"/payment/zk",String.class);
        return result;
    }
}
