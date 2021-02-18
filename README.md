# springcloud
##常用依赖
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid-spring-boot-starter</artifactId>
        <version>1.1.10</version>
    </dependency>
    <!--mysql -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jdbc</artifactId>
    </dependency>
    <!--openfeign -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
    <!--eureka-server -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
    <!--hutool-all -->
    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-all</artifactId>
        <version>5.1.0</version>
    </dependency>
     <!--eureka-client -->
     <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <!--lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <!--封装实体 -->
    <dependency>
        <groupId>com.demo.springcloud</groupId>
        <artifactId>cloud-api-commons</artifactId>
        <version>${project.version}</version>
    </dependency>
    <!--热部署 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>
    <!--zookeeper -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-zookeeper-discovery</artifactId>
    </dependency>
        
## 集群配置
C:\Windows\System32\drivers\etc\hosts
127.0.0.1       eureka7001.com
127.0.0.1       eureka7002.com
127.0.0.1       eureka7003.com
## zookeeper
    配置
    spring:
      #服务别名，注册到zookeeper服务名称
      application:
        name: cloud-consumer-order
      cloud:
        zookeeper:
          connect-string: localhost:2181
          
关闭防火墙命令：
systemctl stop firewalld
查看防火墙命令：
systemctl status firewalld
查看网络是否连通
ping 127.0.0.1
整合zookeeper连接
zookeeper嘉宝冲突
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zookeeper-discovery</artifactId>
    <!-- 先排除自带的zookeeper3.5.3 -->
    <exclusions>
    <exclusion>
        <groupId>org.apache.zookeeper</groupId>
        <artifactId>zookeeper</artifactId>
    </exclusion>
    </exclusions>
</dependency>
<!-- 添加zookeeper3.4.9 -->
<dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>3.4.9</version>
</dependency>
zookeeper服务节点是临时的而不是持久的

## consul
下载consul
https://www.consul.io/downloads
安装步骤consul
https://learn.hashicorp.com/consul
consul使用指南
https://www.springcloud.cc/spring-cloud-consul.html
consul是一套开源的分布式服务发现与配置管理系统
提供了微服务系统中的服务治理，
配置中心，控制总线等功能，
这些功能中的每一个都可以根据需要单独使用，
也可以一起使用以构造全方位网络，
特性：服务发现，健康检查(支持HTTP，TCP，docker，shell脚本定制化)，
键值对存储，多数据中心，可视化web界面
consul与zookeeper差不多，区别依赖包以配置项
依赖
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-discovery</artifactId>
</dependency>
配置
spring:
    cloud:
        consul:
            host: localhost
            port: 8500
            discovery:
                service-name: ${spring.application.name}
## eureka
配置
服务端
eureka:
  instance:
    hostname: eureka7001.com # eureka服务端的实例名称
  client:
    #false表示不向注册中心注册自己
    register-with-eureka: false
    #false表示自己端就是注册中心，我的职责维护服务实例，并不需要去检索服务
    fetch-registry: false
    service-url:
      #设置eureka server交互的地址服务和注册服务都需要的依赖
      #单机配置：defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka
      #指向其他服务
      defaultZone: http://eureka7002.com:7002/eureka
      #指向自己服务
      #defaultZone: http://eureka7001.com:7001/eureka
  server:
    #关闭保护机制，保证不可用服务及时被删除
    enable-self-preservation: false
    eviction-interval-timer-in-ms: 2000
  客户端
  spring:
    application:
      name: cloud-order-service # eureka服务端的实例名称
  
  eureka:
    client:
      #true表示向注册中心注册自己
      register-with-eureka: true
      #是否从服务中心抓取已有的注册信息，默认true，单机无所谓，集群必须为true,才能配合ribbon使用负载均衡
      fetch-registry: true
      service-url:
        #设置eureka server交互的地址服务和注册服务都需要的依赖
        #单机配置：
        #defaultZone: http://localhost:7001/eureka
        #集群配置：
        defaultZone: http://eureka7001.com:7001/eureka,http://eureka7001.com:7002/eureka
    
主要注解
//eureka服务端
@EnableEurekaServer
//eureka客户端
@EnableEurekaClient
//该注解用于consul或zookeeper作为注册中心注册服务
@EnableDiscoveryClient
## ribbon
 @LoadBalanced//支持负载均衡
 比如
 @Configuration
 public class ApplicationContextConfig {
     @Bean
     @LoadBalanced
     public RestTemplate getRestTemplate(){
         return new RestTemplate();
     }
 }
 自定义负载均衡
 @Configuration
 public class MyselfRule {
     @Bean
     public IRule myrule(){
         return new RandomRule();//定义随机
     }
 }
 @SpringBootApplication(exclude = DataSourceAutoConfiguration.class,scanBasePackages={ "com.demo.springcloud"})
 @EnableEurekaClient
 //自定义@RibbonClient(name = "CLOUD-PAYMENT-SERVICE",configuration = MyselfRule.class)
 public class OrderApplication {
     public static void main(String[] ares){
         SpringApplication.run(OrderApplication.class,ares);
     }
 }
 调用其他服务
 //单机调用
 public static final String PAY_URL = "http://cloud-provider-payment";
  @Resource
  private RestTemplate restTemplate;
  常用方法
 restTemplate.getForObject(PAY_URL+"/payment/get",,)
 restTemplate.getForEntity(PAY_URL+"/payment/get",,)
 restTemplate.postForObject(PAY_URL+"/payment/get",,)
 restTemplate.postForEntity(PAY_URL+"/payment/get",,)
 
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
## openfeign
配置
    eureka:
      client:
        #false表示不向注册中心注册自己
        register-with-eureka: false
        service-url:
          #设置eureka server交互的地址服务和注册服务都需要的依赖
          #单机配置：defaultZone: http://localhost:7001/eureka
          defaultZone: http://eureka7001.com:7001/eureka,http://eureka7001.com:7002/eureka
    ribbon:
      # 建立连接所用的时间，适用于网络正常连接，两端连接时间
      ReadTimeout: 5000
      # 建立连接后从服务端读取到可用资源所需的时间
      ConnectTimeout: 5000
    logging:
      level:
        #feign日志以什么级别打印哪个接口
        com.demo.springcloud.service.PaymentFeignService: debug

    /**
     * FEIGN日志打印配置
     */
    @Configuration
    public class FeignConfig {
    
        @Bean
        Logger.Level FeignLoggerLevel(){
            return Logger.Level.FULL;
        }
    }
    
    @SpringBootApplication
    @EnableFeignClients//开启feign
    public class OrderOpenfeignMain80 {
        public static void main(String[] ares){
            SpringApplication.run(OrderOpenfeignMain80.class,ares);
        }
    }
    
    @Component
    @FeignClient(value = "CLOUD-PAYMENT-SERVICE")
    public interface PaymentFeignService {
    
        /**
         * 获取信息
         * @return
         */
        @GetMapping(value = "/payment/get/{id}")
        CommoneResult<Payment> get(@PathVariable("id") Long id);
    
        /**
         * 超时调用
         * @return
         */
        @GetMapping(value = "/payment/feign/timeout")
        String paymentlFeignTimeout();
    }
    
    /**
     * 服务调用
     */
    @RestController
    @Slf4j
    public class OrderFeignController {
    
        @Resource
        private PaymentFeignService paymentFeignService;
    
        /**
         * 获取信息
         * @return
         */
        @GetMapping(value = "/consumerfeign/payment/get/{id}")
        public CommoneResult<Payment> getPaymentInfo(@PathVariable("id") Long id){
            CommoneResult<Payment> paymentCommoneResult = paymentFeignService.get(id);
            return paymentCommoneResult;
        }
    
        /**
         * 超时调用
         * @return
         */
        @GetMapping(value = "/consumerfeign/payment/feign/timeout")
        public String aymentlFeignTimeout(){
            return paymentFeignService.paymentlFeignTimeout();
        }
    }

## hystrix
https://github.com/Netflix/Hystrix

是一个用于处理分布式系统的延迟和容错的开源库，
在分布式系统里，许多依赖不可避免的会调用失败，
比如超时，异常等，能够保证在一个一开出问题的情况下，
不会导致整体服务失败，避免级联故障，以提高分布式系统的弹性，
断路器：本身是一种开关装置，当某个服务单元发生故障之后，通过断路器的故障监控，
向调用方返回一个符合预期的，可处理的备选响应（fallback）,
而不是长时间的等待或者抛出异常调用方无法捕捉的异常，这样就保证了服务调用方的线程不会
被长时间。不必要占用，从而避免了故障在分布式系统中的蔓延，乃至雪崩。

服务降级
以下情况出现：
程序运行异常、超时、服务熔断触发服务降级、线程池/信号量打满也会导致服务降级

服务熔断：类似家里的保险丝，达到最大阈值

服务限流：秒杀高并发等操作
提供者
依赖
 <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
消费方需要加
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>

@EnableCircuitBreaker//开启服务降级

//降级注解，加方法上，注意改value需要重启服务
@HystrixCommand(fallbackMethod = "paymentInfoErroHander",commandProperties = {
    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "3000")
    })
     public String paymentInfoErro(Integer id){
                //计算异常
                //int age = 10/0;
                //超时异常
        //        try {
        //            Integer TIME = 1;
        //            TimeUnit.SECONDS.sleep(TIME);
        //        }catch (InterruptedException e){
        //            e.printStackTrace();
        //        }
                return "线程池："+Thread.currentThread().getName()+"paymentInfoErro："+id;
            }
    /**
     * 服务降级处理
     */
    public String paymentInfoErroHander(Integer id){
        return "线程池："+Thread.currentThread().getName()+"paymentInfoErroHander："+id+"\t"+"请稍后再试！";
    }
    
    sleep interrupted杀死进程
    
消费者
依赖
 <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
@EnableHystrix//开启Hystrix

//降级注解，加方法上，注意改value需要重启服务，自定义捕捉，若配置全局且加自定义捕捉，就会捕捉本身异常
    @HystrixCommand(fallbackMethod = "orderInfoErroHander",commandProperties = {
        @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "1500")
        })
    /**
         * 服务降级处理
         */
        public String orderInfoErroHander(Integer id){
            return "消费方超时，请稍后再试！";
        }
 全局捕捉
 controller类上加
@DefaultProperties(defaultFallback = "globalOrderInfoErroHander")

    /**
     * 全部超时捕捉
     * @param id
     * @return
     */
    @GetMapping(value = "/order/hystrix/globalpaymenterr/{id}")
    @HystrixCommand
    public String paymentInfoGlobalErro(@PathVariable("id")  Integer id){
        return paymentFeignService.paymentInfoErro(id);
    }

     /**
         * 全局服务降级处理
         */
        public String globalOrderInfoErroHander(){
            return "全局消费方超时，请稍后再试！";
        }

 宕机处理异常

 @Component
 @FeignClient(value = "CLOUD-PAYMENT-HYSTRIX",fallback = PaymentFallHystrixService.class)
 public interface PaymentHystrixService {

     @GetMapping(value = "/hystrix/paymentok/{id}")
     String paymentInfoOK(@PathVariable("id")  Integer id);

     @GetMapping(value = "/hystrix/paymenterr/{id}")
     String paymentInfoErro(@PathVariable("id")  Integer id);
 }

 /**
  * 同意捕捉异常超时，宕机
  */
 @Component
 public class PaymentFallHystrixService implements PaymentHystrixService {

     @Override
     public String paymentInfoOK(Integer id) {
         return "paymentInfoOK正常";
     }

     @Override
     public String paymentInfoErro(Integer id) {
         return "paymentInfoErro失败";
     }
 }


