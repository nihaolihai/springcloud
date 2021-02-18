package com.demo.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients//开启feign
@EnableHystrix//开启Hystrix
public class OrderHystrixMain80 {
    public static void main(String[] ares){
        SpringApplication.run(OrderHystrixMain80.class,ares);
    }
}
