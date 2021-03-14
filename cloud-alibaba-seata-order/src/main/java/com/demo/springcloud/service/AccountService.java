package com.demo.springcloud.service;

import com.demo.springcloud.entities.CommoneResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "seata-account-service")
public interface AccountService {

    @PostMapping("/seata/account/divide")
    CommoneResult divide(@RequestParam("userId") Long userId, @RequestParam("money") Integer money);
}
