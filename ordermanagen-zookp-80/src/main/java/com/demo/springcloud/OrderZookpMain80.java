package com.demo.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient//该注解用于consul或zookeeper作为注册中心注册服务
public class OrderZookpMain80 {
    public static void main(String[] ares){
        SpringApplication.run(OrderZookpMain80.class,ares);
    }
}
