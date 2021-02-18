package com.demo.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
 * hystrix
 */
@SpringBootApplication
@EnableHystrixDashboard//开启hystrix仪表盘
public class OrderHystrixDashboard9001 {
    public static void main(String[] ares){
        SpringApplication.run(OrderHystrixDashboard9001.class,ares);
    }

}
