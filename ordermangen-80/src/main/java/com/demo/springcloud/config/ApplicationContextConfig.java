package com.demo.springcloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationContextConfig {
    @Bean
    //@LoadBalanced//使用riboon做负载均衡
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}
