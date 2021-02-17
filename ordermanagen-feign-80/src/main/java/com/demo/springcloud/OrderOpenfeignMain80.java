package com.demo.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients//开启feign
public class OrderOpenfeignMain80 {
    public static void main(String[] ares){
        SpringApplication.run(OrderOpenfeignMain80.class,ares);
    }
}
