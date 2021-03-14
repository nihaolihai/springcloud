package com.demo.springcloud.service;

import com.demo.springcloud.entities.CommoneResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "seata-store-service")
public interface StoreService {

    @PostMapping("/seata/store/divide")
    CommoneResult divide(@RequestParam("productId") Long productId,@RequestParam("count") Integer count);
}
