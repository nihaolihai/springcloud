package com.demo.springcloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope//支持nacos动然刷新功能
@RequestMapping("/api")
public class NacosConfigController {

    @Value("${config.info}")
    private String serverinfo;

    @GetMapping(value = "/configinfo")
    public String getServerInfo(){
        return "欢迎来到nginx反向代理："+serverinfo;
    }
}
