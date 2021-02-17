package com.demo.springcloud.service;

import com.demo.springcloud.entities.CommoneResult;
import com.demo.springcloud.entities.Payment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(value = "CLOUD-PAYMENT-SERVICE")
public interface PaymentFeignService {

    /**
     * 获取信息
     * @return
     */
    @GetMapping(value = "/payment/get/{id}")
    CommoneResult<Payment> get(@PathVariable("id") Long id);

    /**
     * 超时调用
     * @return
     */
    @GetMapping(value = "/payment/feign/timeout")
    String paymentlFeignTimeout();
}
