package com.demo.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient//eureka客户端
@EnableDiscoveryClient//服务信息
public class PaymentApplication {
    public static void main(String[] ares){
        SpringApplication.run(PaymentApplication.class,ares);
    }
}
