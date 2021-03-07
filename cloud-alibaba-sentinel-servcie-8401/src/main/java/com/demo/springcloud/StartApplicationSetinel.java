package com.demo.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class StartApplicationSetinel {

    public static void main(String[] ares){
        SpringApplication.run(StartApplicationSetinel.class,ares);
    }

}
