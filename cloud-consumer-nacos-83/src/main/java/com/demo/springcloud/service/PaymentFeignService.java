package com.demo.springcloud.service;

import com.demo.springcloud.entities.CommoneResult;
import com.demo.springcloud.entities.Payment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "cloud-nacos-provider",fallback = PaymentFeignServiceImpl.class)
public interface PaymentFeignService {

    /**
     * 获取信息
     * @return
     */
    @GetMapping(value = "/providernacos/sentinel/{id}")
    CommoneResult<Payment> getSentinel(@PathVariable("id") String id);

}
