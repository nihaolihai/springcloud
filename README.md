# springcloud

[spring.io](https://spring.io/projects/spring-cloud)

* 服务注册与发现
eureka、zookeeper、consul、nacos
* 服务调用
ribbon、loadbalancer、openfeign
* 服务降级
hystrix、sentinel
* 服务网关
zuul、gateway
* 服务配置
config、nacoa
* 服务总线
bus、nacoa

## hutool

https://www.hutool.cn/docs/#/

##常用依赖
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <!-- 图形化 -->
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
## zookeeper注册中心
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

## consul注册中心
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
## ribbon服务调用
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
## openfeign服务调用
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

## hystrix服务降级
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
     
 服务熔断：类似家里的保险丝，达到最大阈值
 规则：服务降级=》服务熔断=》恢复调用链路
 https://www.martinfowler.com/bliki/CircuitBreaker.html
 
    @HystrixCommand(fallbackMethod = "paymentCircuitBreakerfallback",commandProperties = {
             @HystrixProperty(name = "circuitBreaker.enabled",value = "true"),//是否开启断路器
             @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "10"),//请求次数超过峰值，熔断器将会从关闭到开启
             @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "10000"),//时间窗口期
             @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "60"),//失败率达到多少值跳闸
     })
     public String paymentCircuitBreaker(@PathVariable("id") Integer id){
         if(id<0){
             throw new RuntimeException("****id不能为负数");
         }
         return Thread.currentThread().getName()+"调用成功，流水号："+IdUtil.simpleUUID();
     }
     public String paymentCircuitBreakerfallback(@PathVariable("id") Integer id){
 
         return "****id不能为负数，请稍后再试！"+"\t"+"id："+id;
     }
     
     熔断配置
      Command Properties
      Execution相关的属性的配置：
      hystrix.command.default.execution.isolation.strategy 隔离策略，默认是Thread, 可选Thread｜Semaphore
     
      hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds 命令执行超时时间，默认1000ms
     
      hystrix.command.default.execution.timeout.enabled 执行是否启用超时，默认启用true
      hystrix.command.default.execution.isolation.thread.interruptOnTimeout 发生超时是是否中断，默认true
      hystrix.command.default.execution.isolation.semaphore.maxConcurrentRequests 最大并发请求数，默认10，该参数当使用ExecutionIsolationStrategy.SEMAPHORE策略时才有效。如果达到最大并发请求数，请求会被拒绝。理论上选择semaphore size的原则和选择thread size一致，但选用semaphore时每次执行的单元要比较小且执行速度快（ms级别），否则的话应该用thread。
      semaphore应该占整个容器（tomcat）的线程池的一小部分。
      Fallback相关的属性
      这些参数可以应用于Hystrix的THREAD和SEMAPHORE策略
     
      hystrix.command.default.fallback.isolation.semaphore.maxConcurrentRequests 如果并发数达到该设置值，请求会被拒绝和抛出异常并且fallback不会被调用。默认10
      hystrix.command.default.fallback.enabled 当执行失败或者请求被拒绝，是否会尝试调用hystrixCommand.getFallback() 。默认true
      Circuit Breaker相关的属性
      hystrix.command.default.circuitBreaker.enabled 用来跟踪circuit的健康性，如果未达标则让request短路。默认true
      hystrix.command.default.circuitBreaker.requestVolumeThreshold 一个rolling window内最小的请求数。如果设为20，那么当一个rolling window的时间内（比如说1个rolling window是10秒）收到19个请求，即使19个请求都失败，也不会触发circuit break。默认20
      hystrix.command.default.circuitBreaker.sleepWindowInMilliseconds 触发短路的时间值，当该值设为5000时，则当触发circuit break后的5000毫秒内都会拒绝request，也就是5000毫秒后才会关闭circuit。默认5000
      hystrix.command.default.circuitBreaker.errorThresholdPercentage错误比率阀值，如果错误率>=该值，circuit会被打开，并短路所有请求触发fallback。默认50
      hystrix.command.default.circuitBreaker.forceOpen 强制打开熔断器，如果打开这个开关，那么拒绝所有request，默认false
      hystrix.command.default.circuitBreaker.forceClosed 强制关闭熔断器 如果这个开关打开，circuit将一直关闭且忽略circuitBreaker.errorThresholdPercentage
      Metrics相关参数
      hystrix.command.default.metrics.rollingStats.timeInMilliseconds 设置统计的时间窗口值的，毫秒值，circuit break 的打开会根据1个rolling window的统计来计算。若rolling window被设为10000毫秒，则rolling window会被分成n个buckets，每个bucket包含success，failure，timeout，rejection的次数的统计信息。默认10000
      hystrix.command.default.metrics.rollingStats.numBuckets 设置一个rolling window被划分的数量，若numBuckets＝10，rolling window＝10000，那么一个bucket的时间即1秒。必须符合rolling window % numberBuckets == 0。默认10
      hystrix.command.default.metrics.rollingPercentile.enabled 执行时是否enable指标的计算和跟踪，默认true
      hystrix.command.default.metrics.rollingPercentile.timeInMilliseconds 设置rolling percentile window的时间，默认60000
      hystrix.command.default.metrics.rollingPercentile.numBuckets 设置rolling percentile window的numberBuckets。逻辑同上。默认6
      hystrix.command.default.metrics.rollingPercentile.bucketSize 如果bucket size＝100，window＝10s，若这10s里有500次执行，只有最后100次执行会被统计到bucket里去。增加该值会增加内存开销以及排序的开销。默认100
      hystrix.command.default.metrics.healthSnapshot.intervalInMilliseconds 记录health 快照（用来统计成功和错误绿）的间隔，默认500ms
      Request Context 相关参数
      hystrix.command.default.requestCache.enabled 默认true，需要重载getCacheKey()，返回null时不缓存
      hystrix.command.default.requestLog.enabled 记录日志到HystrixRequestLog，默认true
     
      Collapser Properties 相关参数
      hystrix.collapser.default.maxRequestsInBatch 单次批处理的最大请求数，达到该数量触发批处理，默认Integer.MAX_VALUE
      hystrix.collapser.default.timerDelayInMilliseconds 触发批处理的延迟，也可以为创建批处理的时间＋该值，默认10
      hystrix.collapser.default.requestCache.enabled 是否对HystrixCollapser.execute() and HystrixCollapser.queue()的cache，默认true
     
      ThreadPool 相关参数
      线程数默认值10适用于大部分情况（有时可以设置得更小），如果需要设置得更大，那有个基本得公式可以follow：
      requests per second at peak when healthy × 99th percentile latency in seconds + some breathing room
      每秒最大支撑的请求数 (99%平均响应时间 + 缓存值)
      比如：每秒能处理1000个请求，99%的请求响应时间是60ms，那么公式是：
      （0.060+0.012）
     
      基本得原则时保持线程池尽可能小，他主要是为了释放压力，防止资源被阻塞。
      当一切都是正常的时候，线程池一般仅会有1到2个线程激活来提供服务
     
      hystrix.threadpool.default.coreSize 并发执行的最大线程数，默认10
      hystrix.threadpool.default.maxQueueSize BlockingQueue的最大队列数，当设为－1，会使用SynchronousQueue，值为正时使用LinkedBlcokingQueue。该设置只会在初始化时有效，之后不能修改threadpool的queue size，除非reinitialising thread executor。默认－1。
      hystrix.threadpool.default.queueSizeRejectionThreshold 即使maxQueueSize没有达到，达到queueSizeRejectionThreshold该值后，请求也会被拒绝。因为maxQueueSize不能被动态修改，这个参数将允许我们动态设置该值。if maxQueueSize == -1，该字段将不起作用
      hystrix.threadpool.default.keepAliveTimeMinutes 如果corePoolSize和maxPoolSize设成一样（默认实现）该设置无效。如果通过plugin（https://github.com/Netflix/Hystrix/wiki/Plugins）使用自定义实现，该设置才有用，默认1.
      hystrix.threadpool.default.metrics.rollingStats.timeInMilliseconds 线程池统计指标的时间，默认10000
      hystrix.threadpool.default.metrics.rollingStats.numBuckets 将rolling window划分为n个buckets，默认10
     
 服务限流：秒杀高并发等操作(sentinel)
 
 ## hystrix仪表盘
 
     依赖
     <dependency>
         <groupId>org.springframework.cloud</groupId>
         <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
     </dependency>
     @EnableHystrixDashboard//开启hystrix仪表盘
     
     输入地址
     http://localhost:9001/hystrix
     发送请求查看压力情况
     http://localhost:8001/hystrix.stream
     
##网关gateway
https://spring.io/projects/spring-cloud-gateway#learn
提供一种简单而有效的方式来对api进行路由，以及提供一些强大的过滤功能，
比如：熔断、限流、重试等

    移除web依赖
    依赖
     <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>
    配置
    server:
      port: 9527
    
    spring:
      #禁用缓存
      thymeleaf:
        cache: false
      application:
        name: cloud-gateway-service
      cloud:
        gateway:
          routes:
            - id: payment_routh
              uri: http://localhost:8001
              predicates:
                - Path=/payment/get/**
    
            - id: payment_routh2
              uri: http://localhost:8001
              predicates:
                - Path=/payment/getPaymentlb/**
    
    eureka:
      instance:
        hostname: gateway9527
      client:
        #true表示向注册中心注册自己
        register-with-eureka: true
        #是否从服务中心抓取已有的注册信息，默认true，单机无所谓，集群必须为true,才能配合ribbon使用负载均衡
        fetch-registry: true
        service-url:
          #设置eureka server交互的地址服务和注册服务都需要的依赖
          #单机配置
          defaultZone: http://localhost:7001/eureka
          #集群配置
          #defaultZone: http://eureka7001.com:7001/eureka,http://eureka7001.com:7002/eureka
    
    /**
     * 根据网关做路由配置
     */
    @Configuration
    public class GatewayConfig {
    
        @Bean
        public RouteLocator customRouteLocator(RouteLocatorBuilder routeLocatorBuilder){
            RouteLocatorBuilder.Builder builder = routeLocatorBuilder.routes();
            builder.route("path_route",r ->r.path("/guonei").uri("http://news.baidu.com/guonei")).build();
            return builder.build();
        }
    
        @Bean
        public RouteLocator customRouteLocators(RouteLocatorBuilder routeLocatorBuilder){
            RouteLocatorBuilder.Builder builder = routeLocatorBuilder.routes();
            builder.route("path_routes",r ->r.path("/guoji").uri("http://news.baidu.com/guoji")).build();
            return builder.build();
        }
    }
    
    http://news.baidu.com/
 
 能干什么：反向代理、鉴权、流量控制、熔断、日志监控
 三大核心：route(路由)、predicate(断言)、filter(过滤)
 添加动态配置路由
     
     https://spring.io/projects/spring-cloud-gateway#learn
     提供一种简单而有效的方式来对api进行路由，以及提供一些强大的过滤功能，
     比如：熔断、限流、安全、重试等
     移除web依赖
     依赖
      <dependency>
         <groupId>org.springframework.cloud</groupId>
         <artifactId>spring-cloud-starter-gateway</artifactId>
     </dependency>
     配置
     server:
       port: 9527
     
     spring:
       #禁用缓存
       thymeleaf:
         cache: false
       application:
         name: cloud-gateway-service
       cloud:
         gateway:
           discovery:
             locator:
               enabled: true #开启从注册中心动态创建路由的功能，利用微服务名称进行路由
           routes:
             - id: payment_routh # 路由的ID，没有规定规则但要求唯一
               #uri: http://localhost:8001 # 匹配后提供服务的路由地址
               uri: lb://cloud-payment-service # 提供者服务名称
               predicates:
                 - Path=/payment/get/** # 断言，路径匹配的进行路由
     
             - id: payment_routh2 # 路由的ID，没有规定规则但要求唯一
               #uri: http://localhost:8001 # 匹配后提供服务的路由地址
               uri: lb://cloud-payment-service # 提供者服务名称
               predicates:
                 - Path=/payment/getPaymentlb/**  # 断言，路径匹配的进行路由
    
     eureka:
       instance:
         hostname: gateway9527
       client:
         #true表示向注册中心注册自己
         register-with-eureka: true
         #是否从服务中心抓取已有的注册信息，默认true，单机无所谓，集群必须为true,才能配合ribbon使用负载均衡
         fetch-registry: true
         service-url:
           #设置eureka server交互的地址服务和注册服务都需要的依赖
           #单机配置
           defaultZone: http://localhost:7001/eureka
           #集群配置
           #defaultZone: http://eureka7001.com:7001/eureka,http://eureka7001.com:7002/eureka
     
     http://news.baidu.com/
     
     /**
      * 根据网关做路由配置
      */
     @Configuration
     public class GatewayConfig {
     
         @Bean
         public RouteLocator customRouteLocator(RouteLocatorBuilder routeLocatorBuilder){
             RouteLocatorBuilder.Builder builder = routeLocatorBuilder.routes();
             builder.route("path_route",r ->r.path("/guonei").uri("http://news.baidu.com/guonei")).build();
             return builder.build();
         }
     
         @Bean
         public RouteLocator customRouteLocators(RouteLocatorBuilder routeLocatorBuilder){
             RouteLocatorBuilder.Builder builder = routeLocatorBuilder.routes();
             builder.route("path_routes",r ->r.path("/guoji").uri("http://news.baidu.com/guoji")).build();
             return builder.build();
         }
     }

     访问 
     http://localhost:9527/payment/getPaymentlb
     http://localhost:9527/guoji
     官网
     https://cloud.spring.io/spring-cloud-static/spring-cloud-gateway/2.2.0.RELEASE/reference/html/
     断言：
     ZonedDateTime.now();
     - After=2020-11-10T20:30:07.417+08:00[Asia/Shanghai] #断言，时间在这之后才能执行
     - Before=2020-11-10T20:30:07.417+08:00[Asia/Shanghai] #断言，时间在这之前才能执行
     - Between=2020-11-10T20:30:07.417+08:00[Asia/Shanghai],2020-12-10T20:30:07.417+08:00[Asia/Shanghai] #Between 时间在这之间才能执行
     - Cookie=username,zzyy #Cookie 请求需要带上cookie[key,value正则匹配]
     测试
     curl http://localhost:9527/payment/lb --cookie "username=zzyy"
     - Header=X-Request-Id, \d+  #Header 请求头需要有X-Request-Id属性名,值为整数正则表达式
     测试
     curl http://localhost:9527/payment/lb -H "X-Request-Id:1234"
     - Host=**.atguigu.com #Host 域名匹配
     - Method=GET #Method 方法请求方式匹配
     - Query=username, \d+  #必须带有参数名usernamem，且值为正整数
     过滤
     /**
      * 自定义过滤器
      */
     @Component
     public class MyGobalFilter implements GlobalFilter, Ordered {
     
         @Override
         public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
             String uname = exchange.getRequest().getQueryParams().getFirst("uname");
             if(uname==null){
                 System.out.println("用户名为空");
                 exchange.getResponse().setStatusCode(HttpStatus.NOT_ACCEPTABLE);
                 return exchange.getResponse().setComplete();
             }
             return chain.filter(exchange);
         }
     
         @Override
         public int getOrder() {
             return 0;
         }
     }
 
 ## 分布式配置中心config
 
