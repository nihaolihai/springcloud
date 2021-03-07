package com.demo.springcloud.service;

import com.demo.springcloud.entities.CommoneResult;
import com.demo.springcloud.entities.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentFeignServiceImpl  implements  PaymentFeignService{

    @Override
    public CommoneResult<Payment> getSentinel(String id) {
        return new CommoneResult(444,"利用openfeign调用提供者异常");
    }
}
