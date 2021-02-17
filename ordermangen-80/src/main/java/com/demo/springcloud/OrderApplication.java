package com.demo.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class,scanBasePackages={ "com.demo.springcloud"})
@EnableEurekaClient
public class OrderApplication {
    public static void main(String[] ares){
        SpringApplication.run(OrderApplication.class,ares);
    }
}
