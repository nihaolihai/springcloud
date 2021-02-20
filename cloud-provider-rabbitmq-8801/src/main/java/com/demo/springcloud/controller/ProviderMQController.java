package com.demo.springcloud.controller;

import com.demo.springcloud.service.ProviderMQService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ProviderMQController {

    @Resource
    private ProviderMQService providerMQService;

    @GetMapping(value = "provider/send")
    public String send(){
        return providerMQService.send();
    }
}
