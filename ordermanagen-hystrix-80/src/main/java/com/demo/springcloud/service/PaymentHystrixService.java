package com.demo.springcloud.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(value = "CLOUD-PAYMENT-HYSTRIX",fallback = PaymentFallHystrixService.class)
public interface PaymentHystrixService {

    @GetMapping(value = "/hystrix/paymentok/{id}")
    String paymentInfoOK(@PathVariable("id")  Integer id);

    @GetMapping(value = "/hystrix/paymenterr/{id}")
    String paymentInfoErro(@PathVariable("id")  Integer id);
}
