package com.demo.springcloud.controller;

import com.demo.springcloud.entities.CommoneResult;
import com.demo.springcloud.entities.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * 添加sentinel
 */
@RestController
public class NacosSentinelPaymentController {
    @Value("${server.port}")
    private String serverport;
    public static HashMap<String,Payment> map = new HashMap<>();
    static {
        map.put("1",new Payment(1l,"欢迎来到sentinel1提供者"));
        map.put("2",new Payment(2L,"欢迎来到sentinel2提供者"));
        map.put("3",new Payment(3L,"欢迎来到sentinel3提供者"));
    }

    @GetMapping(value = "/providernacos/sentinel/{string}")
    public CommoneResult<Payment> getSentinel(@PathVariable String string) {
        Payment payment = map.get(string);
        CommoneResult<Payment> result = new CommoneResult(200,"from mysql datasource"+serverport,payment);
        return result;
    }
}
