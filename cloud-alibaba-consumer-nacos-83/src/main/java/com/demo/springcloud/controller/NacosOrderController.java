package com.demo.springcloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@RestController
public class NacosOrderController {

    @Resource
    private RestTemplate restTemplate;

    @Value("${service-url.nacos-user-serive}")
    private String serverUrl;

    @GetMapping(value = "/consumernacos/echo/{string}")
    public String getEcho(@PathVariable String string) {
        return restTemplate.getForObject(serverUrl+"/providernacos/echo/"+string,String.class)+"consumerHello Nacos Discovery " + string;
    }
}
