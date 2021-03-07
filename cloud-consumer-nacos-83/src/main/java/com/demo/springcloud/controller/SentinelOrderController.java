package com.demo.springcloud.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.demo.springcloud.entities.CommoneResult;
import com.demo.springcloud.entities.Payment;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@RestController
public class SentinelOrderController {

    @Resource
    private RestTemplate restTemplate;

    @Value("${service-url.nacos-user-serive}")
    private String serverUrl;

    @GetMapping(value = "/consumernacos/sentinel/{string}")
    //一调用此接口，sentinel就能监控到
    //@SentinelResource(value = "basesentinel")
    //fallback = "handerFallback",管理运行异常
    //@SentinelResource(value = "basesentinel",fallback = "handerFallback")
    //blockHandler = "handerBlockback",管理配置异常
    //@SentinelResource(value = "basesentinel",blockHandler = "handerBlockback")
    //fallback，blockHandler都配，只走blockHandler流控模式,exceptionsToIgnore忽略异常
    @SentinelResource(value = "basesentinel",fallback = "handerFallback",blockHandler = "handerBlockback",
    exceptionsToIgnore = {IllegalAccessException.class,NullPointerException.class})
    public CommoneResult<Payment> getSentinel(@PathVariable String string) {
        CommoneResult<Payment> result = restTemplate.getForObject(serverUrl + "/providernacos/sentinel/" + string, CommoneResult.class, string);
        if(string.equals("4")){
            throw new IllegalArgumentException("非法参数异常");
        }else if(StringUtils.isEmpty(string)){
            throw new NullPointerException("空指针异常");
        }
        return result;
    }

    /**
     * 运行异常
     * @param string
     * @param exception
     * @return
     */
    public CommoneResult handerFallback(@PathVariable String string,Throwable exception){
        Payment payment = new Payment(Long.getLong(string),null);
        return new CommoneResult(444,"兜底异常："+exception.getMessage(),payment);
    }

    /**
     * 配置异常
     * @param string
     * @param exception
     * @return
     */
    public CommoneResult handerBlockback(@PathVariable String string, BlockException exception){
        Payment payment = new Payment(Long.getLong(string),null);
        return new CommoneResult(445,"配置异常："+exception.getClass().getCanonicalName(),payment);
    }

}
