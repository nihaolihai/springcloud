package com.demo.springcloud.service;

import org.springframework.stereotype.Component;

/**
 * 同意捕捉异常超时，宕机
 */
@Component
public class PaymentFallHystrixService implements PaymentHystrixService {

    @Override
    public String paymentInfoOK(Integer id) {
        return "paymentInfoOK正常";
    }

    @Override
    public String paymentInfoErro(Integer id) {
        return "paymentInfoErro失败";
    }
}
