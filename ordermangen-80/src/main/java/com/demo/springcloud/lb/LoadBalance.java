package com.demo.springcloud.lb;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

/**
 * 手写负载均衡算法
 */
public interface LoadBalance {

    ServiceInstance instances(List<ServiceInstance> serviceInstanceList);
}
