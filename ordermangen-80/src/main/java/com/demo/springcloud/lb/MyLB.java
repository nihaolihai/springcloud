package com.demo.springcloud.lb;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MyLB implements LoadBalance {

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    public final int getAndInctance(){
        int current;
        int next;
        do {
            current = this.atomicInteger.get();
            next = current >=2147483647?0:current+1;
        }while (!this.atomicInteger.compareAndSet(current,next));
        System.out.println("next值为："+next);
        return next;
    }
    @Override
    public ServiceInstance instances(List<ServiceInstance> serviceInstanceList) {
        int result = getAndInctance() % serviceInstanceList.size();
        return serviceInstanceList.get(result);
    }
}
