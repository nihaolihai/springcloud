package com.demo.springcloud.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Service;

@Service
public class PayManagenService {

    public String paymentInfoOK(Integer id){
        return "线程池："+Thread.currentThread().getName()+"paymentInfoOK："+id;
    }

    //降级注解
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
}
