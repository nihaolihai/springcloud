package com.demo.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient//该注解用于consul或zookeeper作为注册中心注册服务
public class PaymentMain8003 {
    public static void main(String[] ares){
        SpringApplication.run(PaymentMain8003.class,ares);
    }
}
