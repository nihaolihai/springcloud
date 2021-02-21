package com.demo.springcloud.controller;

import com.demo.springcloud.entities.CommoneResult;
import com.demo.springcloud.entities.Payment;
import com.demo.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class PaymentController {

    @Resource
    private PaymentService paymentService;

    @Value("${server.port}")
    private String serverPort;
    @Resource
    private DiscoveryClient discoveryClient;

    @PostMapping(value = "/payment/create")
    public CommoneResult create(@RequestBody Payment payment){
        int result = paymentService.create(payment);
        log.info("插入结果是："+result);
        if(result>0){
            return new CommoneResult(200,"插入成功,端口号："+serverPort,result);
        }else{
            return new CommoneResult(666,"插入失败",null);
        }
    }

    @GetMapping(value = "/payment/get/{id}")
    public CommoneResult<Payment> get(@PathVariable("id") Long id){
        Payment result = paymentService.getById(id);
        log.info("查询结果是："+id);
        if(result!=null){
            return new CommoneResult(200,"查询成功,端口号："+serverPort,result);
        }else{
            return new CommoneResult(666,"查询失败，传入id："+id,null);
        }
    }

    /**
     * 获取服务列表
     * @return
     */
    @GetMapping(value = "/payment/getDiscover")
    public Object getDiscover(){
        List<String> services = discoveryClient.getServices();
        StringBuilder sb= new StringBuilder();
        StringBuilder sbs= new StringBuilder();
        for (String element:services) {
            log.info("服务列表信息："+element);
            sb.append(element+"\t");
        }
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        for (ServiceInstance serviceInstance:instances) {
            log.info("服务列表信息："+serviceInstance.getServiceId()+"\t"+serviceInstance.getHost()+"\t"+serviceInstance.getUri());
            sbs.append(serviceInstance.getServiceId()+"\t"+serviceInstance.getHost()+"\t"+serviceInstance.getUri());
        }
        return this.discoveryClient;
    }

    /**
     * getPaymentlb
     * @return
     */
    @GetMapping(value = "/payment/getPaymentlb")
    public String getPaymentlb(){
        return serverPort;
    }

    /**
     * 超时调用
     * @return
     */
    @GetMapping(value = "/payment/feign/timeout")
    public String paymentlFeignTimeout(){
        try {
            TimeUnit.SECONDS.sleep(3);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return serverPort;
    }

    /**
     * zipkin
     * @return
     */
    @GetMapping(value = "/payment/getZipkin")
    public String getZipkin(){
        return "welcome to zipkin，来到这里你将是最璀璨的星耀!";
    }
}
