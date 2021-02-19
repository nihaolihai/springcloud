package com.demo.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient//开启eureka客户端
@EnableConfigServer//开启配置中心服务
public class ConfigMain3344 {

    public static void main(String[] ares){
        SpringApplication.run(ConfigMain3344.class,ares);
    }
}
