package com.demo.springcloud.controller;

import com.demo.springcloud.entities.CommoneResult;
import com.demo.springcloud.entities.Payment;
import com.demo.springcloud.service.PaymentFeignService;
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
public class OrderFeignController {

    @Resource
    private PaymentFeignService paymentFeignService;

    /**
     * 获取信息
     * @return
     */
    @GetMapping(value = "/consumerfeign/payment/get/{id}")
    public CommoneResult<Payment> getPaymentInfo(@PathVariable("id") Long id){
        CommoneResult<Payment> paymentCommoneResult = paymentFeignService.get(id);
        return paymentCommoneResult;
    }

    /**
     * 超时调用
     * @return
     */
    @GetMapping(value = "/consumerfeign/payment/feign/timeout")
    public String aymentlFeignTimeout(){
        return paymentFeignService.paymentlFeignTimeout();
    }
}
