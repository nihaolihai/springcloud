package com.demo.springcloud.service;

import cn.hutool.core.util.IdUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Service;

@Service
public class PayManagenService {

    public String paymentInfoOK(Integer id){
        return "线程池："+Thread.currentThread().getName()+"paymentInfoOK："+id;
    }

    //服务降级注解
    @HystrixCommand(fallbackMethod = "paymentInfoErroHander",commandProperties = {
    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "1000")
    })
    public String paymentInfoErro(Integer id){
        //计算异常
        //int age = 10/0;
        //超时异常
//        try {
//            Integer TIME = 1;
//            TimeUnit.SECONDS.sleep(TIME);
//        }catch (InterruptedException e){
//            e.printStackTrace();
//        }
        return "线程池："+Thread.currentThread().getName()+"paymentInfoErro："+id;
    }

    /**
     * 服务降级处理
     */
    public String paymentInfoErroHander(Integer id){
        return "提供方超时，请稍后再试！";
    }

    //服务熔断
    @HystrixCommand(fallbackMethod = "paymentCircuitBreakerfallback",commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled",value = "true"),//是否开启断路器
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "10"),//请求次数超过峰值，熔断器将会从关闭到开启
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "10000"),//时间窗口期
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "60"),//失败率达到多少值跳闸
    })
    public String paymentCircuitBreaker(Integer id){
        if(id<0){
            throw new RuntimeException("****id不能为负数");
        }
        return Thread.currentThread().getName()+"调用成功，流水号："+IdUtil.simpleUUID();
    }
    public String paymentCircuitBreakerfallback(Integer id){

        return "****id不能为负数，请稍后再试！"+"\t"+"id："+id;
    }

}
