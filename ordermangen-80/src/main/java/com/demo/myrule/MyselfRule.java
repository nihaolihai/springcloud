package com.demo.myrule;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * irule规则
 * 默认为 new RoundRobinRule()轮训
 */
@Configuration
public class MyselfRule {

    @Bean
    public IRule myrule(){
        return new RandomRule();//定义随机
    }
}
