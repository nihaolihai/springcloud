package com.demo.springcloud.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "sentinel/api")
public class FlowLimitController {

    @Value("${server.port}")
    private String serverport;

    /**
     * 流量控制规则：
     * 1.资源名：接口名称，比如getMapping("/testa")
     * 2.针对来源：defalut
     * 3.阈值类型：qps(秒)，线程数(多线程调用)；单机阈值：1
     * 4.是否集群
     * 5.流控模式：直接，关联(当testa关联testb后，用多线程调用testb,此时调用testa就会报错)，链路
     * 6.流控效果：
     * 1)快速失败
     * 2)warm up(案例：阈值为10+预热时长5秒
     * 系统初始化的阈值为10/约等于3，即阈值刚开始为3，然后过了5秒后阈值才慢慢恢复为10)，
     * 3)排队对待：/testa每秒1次请求，超过的话就排队等待，等待时间为20000毫秒
     * 表示1秒钟内查询1次就是OK，若超过一次，就直接快速失败，报默认错误
     * @return
     */
    @GetMapping("/testa")
    public String getTesta(){
        return "*******A*******"+serverport;
    }

    @GetMapping("/testb")
    public String getTestb(){
        return "*******B*******"+serverport;
    }

    /**
     * testa关联testc，当testc挂了，则testa直接报错
     * @return
     */
    @GetMapping("/testc")
    public String getTestc(){
        return "*******C*******"+serverport;
    }

    /**
     * 降级:RD
     * 1秒持续进入5个请求平均响应时间(单位毫秒)>阈值->触发降级(断路器打开)->时间窗口结果->关闭降级
     * 登录sentinel新增降级规则：RT=200ms（0.2秒就要程序处理完成）,时间窗口=1s
     * @return
     */
    @GetMapping("/testd")
    public String getTestd(){
        try {
            Thread.sleep(1);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return "*******RD:D*******"+serverport;
    }
    /**
     * 降级:异常比例
     * qps>=5 && 异常比例(秒级统计)超过阈值->触发降级(断路器打开)->时间窗口结果->关闭降级
     * 登录sentinel新增降级规则：阈值=0.2,时间窗口=3s
     * @return
     */
    @GetMapping("/teste")
    public String getTeste(){
        int age = 10/0;
        return "*******RT：E*******";
    }
    /**
     * 降级:异常数
     * 异常数(分钟级统计)超过阈值->触发降级(断路器打开)->时间窗口结果->关闭降级
     * 登录sentinel新增降级规则：异常数=5(请求次数),时间窗口=61s
     * @return
     */
    @GetMapping("/testf")
    public String getTestf(){
        int age = 10/0;
        return "*******异常数：F*******";
    }
    /**
     * 热点规则
     * 登录sentinel新增热点规则：
     * 资源名称：testhostkey
     * 参数索引：0
     * 流控模式：qps(秒级)
     * 阈值：1(1秒调用次数)
     * 是否集群：否
     * 例外项数目：0
     * 热点基本类型及string
     * @return
     */
    @GetMapping("/testhostkey")
    @SentinelResource(value = "testhostkey",blockHandler = "defalutblockex")
    public String getTestHostKey(@RequestParam(value = "key1",required = false) String key1,
                                 @RequestParam(value = "key2",required = false) String key2){
        return "******热点规则********";
    }
    public String defalutblockex(String key1, String key2, BlockException blockException){
        return "******热点规则,异常处理提示********";
    }
}
