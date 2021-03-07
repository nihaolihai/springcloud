package com.demo.springcloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "sentinel/api")
public class FlowLimitController {

    @Value("${server.port}")
    private String serverport;

    @GetMapping("/testa")
    public String getTesta(){
        return "*******A*******"+serverport;
    }

    @GetMapping("/testb")
    public String getTestb(){
        return "*******B*******"+serverport;
    }
}
