package com.demo.springcloud.controller;

import com.demo.springcloud.entities.CommoneResult;
import com.demo.springcloud.entities.Payment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@RestController
public class OrderManagenController {

    //单机调用
    //public static final String PAY_URL = "http://localhost:8001";
    //集群调用
    public static final String PAY_URL = "http://CLOUD-PAYMENT-SERVICE";

    @Resource
    private RestTemplate restTemplate;

    @PostMapping(value = "/order/payment/create")
    public CommoneResult<Payment> create(Payment payment){
        return restTemplate.postForObject(PAY_URL+"payment/create",payment,CommoneResult.class);
    }

    /**
     * restTemplate.getForObject
     * @param id
     * @return
     */
    @GetMapping(value = "/order/payment/get/{id}")
    public CommoneResult<Payment> get(@PathVariable("id") Long id){
        return restTemplate.getForObject(PAY_URL+"/payment/get/"+id,CommoneResult.class);
    }

    /**
     * restTemplate.getForEntity
     * @param id
     * @return
     */
    @GetMapping(value = "/order/payment/getForEntity/{id}")
    public CommoneResult<Payment> getForEntity(@PathVariable("id") Long id){
        ResponseEntity<CommoneResult> forEntity = restTemplate.getForEntity(PAY_URL + "/payment/get/" + id, CommoneResult.class);
        if(forEntity.getStatusCode().is2xxSuccessful()){
            return forEntity.getBody();
        }else{
            return new CommoneResult(666,"查询失败",null);
        }
    }
}
