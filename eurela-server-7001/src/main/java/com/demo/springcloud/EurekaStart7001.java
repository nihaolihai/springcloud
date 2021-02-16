package com.demo.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 集群
 * C:\Windows\System32\drivers\etc\hosts
 * ###################springcloud##############
 * 127.0.0.1       eureka7001.com
 * 127.0.0.1       eureka7002.com
 * 127.0.0.1       eureka7003.com
 *
 * 127.0.0.1       myzuul.com
 * 127.0.0.1       config-3344.com
 * 127.0.0.1       client-config.com
 *
 * 127.0.0.1       cloud-provider-payment
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaStart7001 {
    public static void main(String[] ares){
        SpringApplication.run(EurekaStart7001.class,ares);
    }

}
