package com.demo.springcloud.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.demo.springcloud.entities.CommoneResult;
import com.demo.springcloud.entities.Payment;
import com.demo.springcloud.myhander.CustomerBloclHander;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "sentinel/apis")
public class RateLimitController {

    /**
     * @SentinelResource
     * 按资源名称限流测试OK
     * 登录sentinel：添加资源名称：testresource，一调用后就会有
     * 新增流量控制规则
     * 1.资源名：testresource
     * 2.针对来源：defalut
     * 3.阈值类型：qps(秒)，线程数(多线程调用)；单机阈值：1
     * @return
     */
    @GetMapping("/testresource")
    @SentinelResource(value = "testresource",blockHandler = "handerException")
    public CommoneResult getTestResource(){
        return new CommoneResult(200,"按资源名称限流测试OK",new Payment(2020L,"test"));
    }
    public CommoneResult handerException(BlockException blockException){
        return new CommoneResult(444,blockException.getClass().getCanonicalName()+"\t 服务不可用");
    }

    /**
     * 不写blockHandler，默认自带
     * @return
     */
    @GetMapping("/testresource/byurl")
    @SentinelResource(value = "byurl",blockHandler = "handerExceptions")
    public CommoneResult getTestResourceByurl(){
        return new CommoneResult(200,"按资源url限流测试OK",new Payment(2020L,"test"));
    }
    public CommoneResult handerExceptions(BlockException blockException){
        return new CommoneResult(444,blockException.getClass().getCanonicalName()+"\t 服务不可用");
    }

    /**
     * 自定义异常
     */
    @GetMapping("/testresource/customerBloclHander")
    @SentinelResource(value = "customerBloclHander",blockHandlerClass = CustomerBloclHander.class,blockHandler = "handerExcetion")
    public CommoneResult getTestCustomerBloclHander(){
        return new CommoneResult(200,"按资源customerBloclHander限流测试OK",new Payment(2020L,"test"));
    }
}
