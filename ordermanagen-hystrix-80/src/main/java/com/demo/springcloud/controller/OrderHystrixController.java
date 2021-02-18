package com.demo.springcloud.controller;

import com.demo.springcloud.service.PaymentHystrixService;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 服务调用
 */
@RestController
@Slf4j
@DefaultProperties(defaultFallback = "globalOrderInfoErroHander")
public class OrderHystrixController {

    @Resource
    private PaymentHystrixService paymentFeignService;

    @GetMapping(value = "/order/hystrix/paymentok/{id}")
    public String paymentInfoOK(@PathVariable("id")  Integer id){
        return paymentFeignService.paymentInfoOK(id);
    }

    /**
     * 自定义超时捕捉
     * @param id
     * @return
     */
    @GetMapping(value = "/order/hystrix/paymenterr/{id}")
    @HystrixCommand(fallbackMethod = "orderInfoErroHander",commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "1500")
    })
    public String paymentInfoErro(@PathVariable("id")  Integer id){
        return paymentFeignService.paymentInfoErro(id);
    }

    /**
     * 全部超时捕捉
     * @param id
     * @return
     */
    @GetMapping(value = "/order/hystrix/globalpaymenterr/{id}")
    @HystrixCommand
    public String paymentInfoGlobalErro(@PathVariable("id")  Integer id){
        return paymentFeignService.paymentInfoErro(id);
    }

    /**
     * 自定义服务降级处理
     */
    public String orderInfoErroHander(@PathVariable("id")  Integer id){
        return "消费方超时，请稍后再试！";
    }

    /**
     * 全局服务降级处理
     */
    public String globalOrderInfoErroHander(){
        return "全局消费方超时，请稍后再试！";
    }
}
