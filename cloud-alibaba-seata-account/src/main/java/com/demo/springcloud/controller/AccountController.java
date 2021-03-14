package com.demo.springcloud.controller;

import com.demo.springcloud.entities.Account;
import com.demo.springcloud.entities.CommoneResult;
import com.demo.springcloud.service.AccountService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class AccountController {

    @Resource
    private AccountService accountService;

    @PostMapping("/seata/account/divide")
    public CommoneResult divide(@RequestParam("userId") Long userId,@RequestParam("money") Integer money){
        int result = accountService.divide(userId,money);
        if(result>0){
            return new CommoneResult(200,"插入成功",result);
        }else{
            return new CommoneResult(666,"插入失败",null);
        }
    }

    @PostMapping(value = "/seata/account/create")
    public CommoneResult create(@RequestBody Account account){
        int result = accountService.create(account);
        if(result>0){
            return new CommoneResult(200,"插入成功",result);
        }else{
            return new CommoneResult(666,"插入失败",null);
        }
    }

    @GetMapping(value = "/seata/account/get/{id}")
    public CommoneResult<Account> get(@PathVariable("id") Long id){
        Account result = accountService.getById(id);
        if(result!=null){
            return new CommoneResult(200,"查询成功,结果：",result);
        }else{
            return new CommoneResult(666,"查询失败，传入id："+id,null);
        }
    }

}
