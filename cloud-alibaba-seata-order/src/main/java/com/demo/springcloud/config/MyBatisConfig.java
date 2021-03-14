package com.demo.springcloud.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cache.annotation.CacheConfig;

@CacheConfig
@MapperScan({"com.demo.springcloud.dao"})
public class MyBatisConfig {
}
