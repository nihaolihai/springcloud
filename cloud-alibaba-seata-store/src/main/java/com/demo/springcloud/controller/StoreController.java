package com.demo.springcloud.controller;

import com.demo.springcloud.entities.CommoneResult;
import com.demo.springcloud.entities.Store;
import com.demo.springcloud.service.StoreService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class StoreController {

    @Resource
    private StoreService storeService;

    @PostMapping(value = "/seata/store/create")
    public CommoneResult create(@RequestBody Store store){
        int result = storeService.create(store);
        if(result>0){
            return new CommoneResult(200,"插入成功",result);
        }else{
            return new CommoneResult(666,"插入失败",null);
        }
    }

    @GetMapping(value = "/seata/store/get/{id}")
    public CommoneResult<Store> get(@PathVariable("id") Long id){
        Store result = storeService.getById(id);
        if(result!=null){
            return new CommoneResult(200,"查询成功,结果：",result);
        }else{
            return new CommoneResult(666,"查询失败，传入id："+id,null);
        }
    }

}
