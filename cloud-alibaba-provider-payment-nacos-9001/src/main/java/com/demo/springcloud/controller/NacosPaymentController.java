package com.demo.springcloud.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NacosPaymentController {

    @GetMapping(value = "/providernacos/echo/{string}")
    public String echo(@PathVariable String string) {
        return "9001Hello Nacos Discovery " + string;
    }
}
