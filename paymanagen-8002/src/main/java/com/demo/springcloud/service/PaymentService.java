package com.demo.springcloud.service;

import com.demo.springcloud.entities.Payment;

public interface PaymentService {
    int create(Payment payment);
    Payment getById(Long id);
}
