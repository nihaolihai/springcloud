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

## java工具库hutool

https://www.hutool.cn/docs/#/

## springboot token
    依赖
    <parent>
        <artifactId>daycloud</artifactId>
        <groupId>com.demo.springcloud</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>
    <groupId>mydlq.club</groupId>
    <artifactId>springboot-redis-token</artifactId>
    <version>0.0.1</version>
    <name>springboot-redis-token</name>
    <description>Idempotent Demo</description>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <!--springboot web-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!--springboot data redis-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>
        <!--lombok-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

    配置
    spring:
      redis:
        ssl: false
        host: 127.0.0.1
        port: 6379
        database: 0
        timeout: 1000
        password:
        lettuce:
          pool:
            max-active: 100
            max-wait: -1
            min-idle: 0
            max-idle: 20

    工具
    @Slf4j
    @Service
    public class TokenUtilService {
    
        @Autowired
        private StringRedisTemplate redisTemplate;
    
        /**
         * 存入 Redis 的 Token 键的前缀
         */
        private static final String IDEMPOTENT_TOKEN_PREFIX = "idempotent_token:";
    
        /**
         * 创建 Token 存入 Redis，并返回该 Token
         *
         * @param value 用于辅助验证的 value 值
         * @return 生成的 Token 串
         */
        public String generateToken(String value) {
            // 实例化生成 ID 工具对象
            String token = UUID.randomUUID().toString();
            // 设置存入 Redis 的 Key
            String key = IDEMPOTENT_TOKEN_PREFIX + token;
            // 存储 Token 到 Redis，且设置过期时间为5分钟
            redisTemplate.opsForValue().set(key, value, 5, TimeUnit.MINUTES);
            // 返回 Token
            return token;
        }
    
        /**
         * 验证 Token 正确性
         *
         * @param token token 字符串
         * @param value value 存储在Redis中的辅助验证信息
         * @return 验证结果
         */
        public boolean validToken(String token, String value) {
            // 设置 Lua 脚本，其中 KEYS[1] 是 key，KEYS[2] 是 value
            String script = "if redis.call('get', KEYS[1]) == KEYS[2] then return redis.call('del', KEYS[1]) else return 0 end";
            RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
            // 根据 Key 前缀拼接 Key
            String key = IDEMPOTENT_TOKEN_PREFIX + token;
            // 执行 Lua 脚本
            Long result = redisTemplate.execute(redisScript, Arrays.asList(key, value));
            // 根据返回结果判断是否成功成功匹配并删除 Redis 键值对，若果结果不为空和0，则验证通过
            if (result != null && result != 0L) {
                log.info("验证 token={},key={},value={} 成功", token, key, value);
                return true;
            }
            log.info("验证 token={},key={},value={} 失败", token, key, value);
            return false;
        }
    
    }
    
    接口调用
    @Slf4j
    @RestController
    public class TokenController {
    
        @Autowired
        private TokenUtilService tokenService;
    
        /**
         * 获取 Token 接口
         *
         * @return Token 串
         */
        @GetMapping("/token")
        public String getToken() {
            // 获取用户信息（这里使用模拟数据）
            // 注：这里存储该内容只是举例，其作用为辅助验证，使其验证逻辑更安全，如这里存储用户信息，其目的为:
            // - 1)、使用"token"验证 Redis 中是否存在对应的 Key
            // - 2)、使用"用户信息"验证 Redis 的 Value 是否匹配。
            String userInfo = "mydlq";
            // 获取 Token 字符串，并返回
            return tokenService.generateToken(userInfo);
        }
    
        /**
         * 接口幂等性测试接口
         *
         * @param token 幂等 Token 串
         * @return 执行结果
         */
        @PostMapping("/test")
        public String test(@RequestHeader(value = "token") String token) {
            // 获取用户信息（这里使用模拟数据）
            String userInfo = "mydlq";
            // 根据 Token 和与用户相关的信息到 Redis 验证是否存在对应的信息
            boolean result = tokenService.validToken(token, userInfo);
            // 根据验证结果响应不同信息
            return result ? "正常调用" : "重复调用";
        }
    
    }
    
    启动类
    @SpringBootApplication
    public class Application {
    
        public static void main(String[] args) {
            SpringApplication.run(Application.class, args);
        }
    
    }
    mock测试类
    @Slf4j
    @SpringBootTest
    @RunWith(SpringRunner.class)
    public class IdempotenceTest {
    
        @Autowired
        private WebApplicationContext webApplicationContext;
    
        @Test
        public void interfaceIdempotenceTest() throws Exception {
            // 初始化 MockMvc
            MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
            // 调用获取 Token 接口
            String token = mockMvc.perform(MockMvcRequestBuilders.get("/token")
                    .accept(MediaType.TEXT_HTML))
                    .andReturn()
                    .getResponse().getContentAsString();
            log.info("获取的 Token 串：{}", token);
            // 循环调用 5 次进行测试
            for (int i = 1; i <= 5; i++) {
                log.info("第{}次调用测试接口", i);
                // 调用验证接口并打印结果
                String result = mockMvc.perform(MockMvcRequestBuilders.post("/test")
                        .header("token", token)
                        .accept(MediaType.TEXT_HTML))
                        .andReturn().getResponse().getContentAsString();
                log.info(result);
    //            // 结果断言
    //            if (i == 0) {
    //                System.out.println("调用返回值："+result+"\t"+"正常调用");
    //                //Assert.assertEquals(result, "正常调用");
    //            } else {
    //                System.out.println("调用返回值："+result+"\t"+"重复调用");
    //                //Assert.assertEquals(result, "重复调用");
    //            }
            }
        }
    
    }

## 常用依赖
    <dependencies>
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
    <!--springboot data redis-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-pool2</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
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
    <!--java工具库hutool-all -->
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
    <!--gateway 无需加web,actuator依赖 -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>
    <!--hystrix -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
    </dependency> 
    <!--hystrix-dashboard仪表盘 -->
     <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
    </dependency> 
    </dependencies>
    <build>
        <plugins>
          <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
              <fork>true</fork>
              <addResources>true</addResources>
            </configuration>
          </plugin>
        </plugins>
    </build> 
    <!--删除多余依赖 -->
    <dependency>
       <groupId>ms.platform</groupId>
       <artifactId>ops-rpc-client</artifactId>
       <version>1.1.7</version>
       <exclusions>
          <exclusion>
             <groupId>ch.qos.logback</groupId>
             <artifactId>logback-core</artifactId>
          </exclusion>
          <exclusion>
             <groupId>ch.qos.logback</groupId>
             <artifactId>logback-classic</artifactId>
          </exclusion>
       </exclusions>
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
## eureka注册中心

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
     
## 服务网关gateway
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
 * 集中配置文件
 * 不同环境不同配置，动态化配置更新，分环境部署
 * 比如
 1.dev
 2.test
 3.pro
 4.beta
 5.release
 * 运行期间动态调整配置，不在需要在每个服务部署的机器上编写配置文件，服务会向配置中心统一拉取配置自己的信息
 * 当配置发生变动时，服务不需要重启即可感知到配置的变化并应用新的的配置
 * 将配置信息以REST接口的形式暴露
 
       
       依赖
        <dependencies>
          <!--config-server -->
         <dependency>
             <groupId>org.springframework.cloud</groupId>
             <artifactId>spring-cloud-config-server</artifactId>
         </dependency>
         <!--eureka-client -->
         <dependency>
             <groupId>org.springframework.cloud</groupId>
             <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
         </dependency>
         <dependency>
             <groupId>org.springframework.boot</groupId>
             <artifactId>spring-boot-starter-web</artifactId>
         </dependency>
         <!-- 图形化 -->
         <dependency>
             <groupId>org.springframework.boot</groupId>
             <artifactId>spring-boot-starter-actuator</artifactId>
         </dependency>
         <!--热部署 -->
         <dependency>
             <groupId>org.springframework.boot</groupId>
             <artifactId>spring-boot-devtools</artifactId>
             <scope>runtime</scope>
             <optional>true</optional>
         </dependency>
         <!--lombok -->
         <dependency>
             <groupId>org.projectlombok</groupId>
             <artifactId>lombok</artifactId>
             <optional>true</optional>
         </dependency>
     </dependencies>
 
     配置
     
     spring:
       #服务别名，注册到eureka服务名称
       application:
         name: cloud-config-service
       cloud:
         config:
           server:
             git:
               uri: https://github.com/nihaolihai/springcloud-config.git
               search-paths:
                 - springcloud-config
           label: master
     
     eureka:
       client:
         service-url:
           defaultZone: http://localhost:7001/eureka
     
     启动类
     @SpringBootApplication
     @EnableEurekaClient//开启eureka客户端
     @EnableConfigServer//开启配置中心服务
     public class ConfigMain3344 {
     
         public static void main(String[] ares){
             SpringApplication.run(ConfigMain3344.class,ares);
         }
     }
     
     访问
     http://config-3344.com:3344/config-test.yml
     或者
     http://config-3344.com:3344/config/test
     
    客户端
    application.yml是用户级的资源配置项
    boostrap.yml是系统级的，优先级更高
    
    依赖
    
    <dependencies>
            <!--config -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-config</artifactId>
                <version>2.2.2.RELEASE</version>
            </dependency>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
            </dependency>
            <!-- 图形化 -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-actuator</artifactId>
            </dependency>
            <!--热部署 -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-devtools</artifactId>
                <scope>runtime</scope>
                <optional>true</optional>
            </dependency>
        </dependencies>
    
    配置
    
    bootstrap.yml
    server:
      port: 3355
    
    spring:
      #服务别名，注册到eureka服务名称
      application:
        name: cloud-config-client
      cloud:
        config:
          label: master # 分支名称
          name: config # 配置文件名称
          profile: dev # 读取后缀名称
          uri: http://localhost:3344 # 配置中心地址
    
    eureka:
      client:
        service-url:
          defaultZone: http://localhost:7001/eureka
    
    eureka:
      client:
        service-url:
          defaultZone: http://localhost:7001/eureka
    
    启动类
    @SpringBootApplication
    @EnableEurekaClient
    public class ConfigClient3355 {
        public static void main(String[] ares){
            SpringApplication.run(ConfigClient3355.class,ares);
        }
    }
    
    在controller加@RefreshScope注解
    
    @RestController
    @RefreshScope
    public class ConfigController {
    
        @Value("${config.info}")
        private String serverinfo;
    
        @GetMapping(value = "/configinfo")
        public String getServerInfo(){
            return serverinfo;
        }
    }
    
    访问
    http://localhost:3355/configinfo
    手动改配置中心服务端信息
    需手动调post去刷新
    curl -X POST "http://localhost:3355/actuator/refresh"
    想自动获取最新的配置则要用到消息总线bus
    
 ## 消息总线bus,支持rabbit,kafka
     rabbitmq
    安装erlang
    http://erlang.org/download/otp_win64_21.3.exe
    安装好erlang环境后才能使用rabbitmq
    下载rabbitmq
    https://www.rabbitmq.com/download.html
    cd sbin
    执行：rabbitmq-plugins enable rabbitmq_management
    输入http://127.0.0.1:15672/
    常用命令
    关闭MQ rabbitmqctl stop
    使用命令添加用户并授权
    添加用户
    rabbitmqctl add_user admin admin
    设置permissions
    rabbitmqctl set_permissions -p "/" admin ".*" ".*" ".*"
    设置用户角色
    rabbitmqctl set_user_tags admin administrator
    查看新添加的admin
    rabbitmqctl list_users
    查看用于的权限
    rabbitmqctl list_permissions -p /

    <!-- rabbitmq -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-bus-amqp</artifactId>
    </dependency>
    
    服务端添加配置
    rabbitmq:
      host: localhost
      port: 5672
      username: guest
      password: guest
      
    暴露配置bus刷新的端点
    management:
        endpoints:
        web:
            exposure:
                include: 'bus-refresh'
                
    客户端
    
    在controller加@RefreshScope注解
    
    rabbitmq配置
      rabbitmq:
        host: localhost
        port: 5672
        username: guest
        password: guest
        
    management:
      endpoints:
        web:
          exposure:
            include: '*'
 
    需手动调post去刷新
    curl -X POST "http://localhost:3344/bus-refresh"
    一次请求全部通知，广播式  
    通知指定一个实例
    curl -X POST "http://localhost:3344/actuator/bus-refresh/cloud-config-client:3355" 

## 消息驱动 stream
屏蔽底层消息中间件的差异，降低切换成本，
统一线下的编程模型

    依赖
    <dependencies>
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
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
    </dependencies>

    配置
    spring:
      application:
        name: cloud-provider-rabbitmq
      cloud:
        stream:
          binders: #在此绑定rabbitmq的服务信息
            defaultRabbit: #表示定义的名称，相当于binder整合
              type: rabbit #消息组件类型
              environment: #设置rabbitmq的相关的环境配置
                spring:
                  rabbitmq:
                    host: localhost
                    port: 5672
                    username: guest
                    password: guest
          bindings: #服务的整合处理
          # input: #这个名字是一个消费者通道的名称
            output: #这个名字是一个提供者通道的名称
              destination: studyExchange #表示要使用的exchange名称定义
              content-type: application/json #设置信息类型，本次为json,文本则设置“text/plain”
              binder: defaultRabbit #设置要绑定的信息服务的具体设置
			  #group: cosum8901
			  group: cosum8902
    
    
    eureka:
      client:
        #true表示向注册中心注册自己
        register-with-eureka: true
        #是否从服务中心抓取已有的注册信息，默认true，单机无所谓，集群必须为true,才能配合ribbon使用负载均衡
        fetch-registry: true
        service-url:
          #设置eureka server交互的地址服务和注册服务都需要的依赖
          #单机配置：
          defaultZone: http://localhost:7001/eureka
          #集群设置
          #defaultZone: http://eureka7001.com:7001/eureka,http://eureka7001.com:7002/eureka
      instance:
        instance-id: send-8801.com
        prefer-ip-address: true #显示主机IP显示
        #eureka客户端向服务端发送心跳的时间间隔，单位为妙(默认30s)
        lease-renewal-interval-in-seconds: 2
        #eureka服务端在收到最后一次心跳后等待时间上限，单位为妙(默认90s)，超时将剔除服务
        lease-expiration-duration-in-seconds: 5
        
    @EnableBinding(Source.class)//定义消息的推送管道
    public class ProviderMQServiceImpl implements ProviderMQService {
    
        @Resource
        private MessageChannel output;//消息发送官大
    
        /**
         * 发送信息
         * @return
         */
        @Override
        public String send() {
            String value = UUID.randomUUID().toString();
            output.send(MessageBuilder.withPayload(value).build());
            return value;
        }
    }
    调用
    @RestController
    public class ProviderMQController {
    
        @Resource
        private ProviderMQService providerMQService;
    
        @GetMapping(value = "provider/send")
        public String send(){
            return providerMQService.send();
        }
    }
    消费者
    @Component
    @EnableBinding(Sink.class)//定义消息的消费管道
    public class ProviderMQController {
    
        @Value("${server.port}")
        private String serverport;
    
        @StreamListener(Sink.INPUT)
        public void receive(Message<String> message){
            System.out.println(message.getPayload());
        }
    }
    
    如何解决重复性消费?
    需要用到group: cosum8902
    
## 分布式请求链路跟踪 sleuth
提供一套服务跟踪的解决方案
springcloud从F版起已不需要构建zipkin sever,只需调用jar包即可。
下载zipkin(2.12.9/zipkin-server-2.12.9-exec.jar)

https://dl.bintray.com/openzipkin/maven/io/zipkin/java/zipkin-server/

运行：java -jar zipkin-server-2.12.9-exec.jar

访问：localhost:9411/zipkin/

    依赖
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-zipkin</artifactId>
    </dependency>
    
    配置
    spring:
        zipkin:
            base-url: http://localhost:9411
        sleuth:
          sampler:
            probability: 1 #采样率值介于0和1，1代表全部采集
## springcloud alibaba
[中文官网](https://github.com/alibaba/spring-cloud-alibaba/blob/master/README-zh.md)
[英文官网](https://spring-cloud-alibaba-group.github.io/github-pages/greenwich/spring-cloud-alibaba.html)

能干什么？？？
* 服务限流降级：默认支持servlet,feign,resttemplete,dubbo,rocketmq,小牛柳降级功能的接入，
可以在运行时通过控制台实时修改限流降级规则，还支持查看限流降级metrics监控
* 服务注册与发现：适配springcloud服务注册与发现标准，默认集成了ribbon支持
* 分布式配置管理：支持分布式系统中的外部化配置，配置更改时自动刷新
* 消息驱动能力基于springcloud stream为微服务应用构建消息驱动能力
* 阿里云对象存储：阿里云提供的海量、安全、低成本、高并发的云存储服务，
支持在任何应用、任何时间、任何地点存储和访问人员类型的数据
* 分布式任务调度：提供秒级、精准、高可靠、高可用的定时(基于cron表达式)任务调度服务，
同时通过分布式的任务执行模型，如网络任务。网格任务支持海量子任务均匀分配到所有worker(schedulerx-client)上执行

### nacos(dynamic naming and configuration service)
nacos是一个更易于构建云原生应用的动态服务发现、配置管理和服务管理平台,
就是注册中心+配置中心的组合，等价于nacos=eureka+config+bus
替代eureka做服务注册中心，替代config做配置中心

[nacos文档](https://nacos.io/en-us/)

[下载nacos](https://github.com/alibaba/nacos/releases)

直接运行bin下的startup.cmd
访问：localhost:8848/nacos
    
    提供者
    依赖
    <dependencies>
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
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.demo.springcloud</groupId>
            <artifactId>cloud-api-commons</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-commons</artifactId>
            <version>2.2.1.RELEASE</version>
        </dependency>
    </dependencies>
    配置
    spring:
      application:
        name: cloud-nacos-provider
      cloud:
        nacos:
          discovery:
            server-addr: localhost:8848 #设置nacos地址

    management:
      endpoints:
      web:
        exposure:
          include: '*'

    启动类
        @SpringBootApplication
        @EnableDiscoveryClient
        public class NacosProviderDemoApplication9001 {

            public static void main(String[] args) {
                SpringApplication.run(NacosProviderDemoApplication9001.class, args);
            }

        }

        @RestController
        public class NacosPaymentController {

            @GetMapping(value = "/providernacos/echo/{string}")
            public String echo(@PathVariable String string) {
                return "9001Hello Nacos Discovery " + string;
            }
        }
    
    消费者
    依赖
    <dependencies>
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
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-devtools</artifactId>
                <scope>runtime</scope>
                <optional>true</optional>
            </dependency>
            <dependency>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <optional>true</optional>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-test</artifactId>
                <scope>test</scope>
            </dependency>
            <dependency>
                <groupId>com.demo.springcloud</groupId>
                <artifactId>cloud-api-commons</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
            </dependency>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-commons</artifactId>
                <version>2.2.1.RELEASE</version>
            </dependency>
        </dependencies>

    配置
    spring:
      application:
        name: cloud-nacos-consumer
      cloud:
        nacos:
          discovery:
            server-addr: localhost:8848 #设置nacos地址

    service-url:
      nacos-user-serive: http://cloud-nacos-provider


    @SpringBootApplication
    @EnableDiscoveryClient
    public class NacosConsumerDemoApplication83 {

        public static void main(String[] args) {
            SpringApplication.run(NacosConsumerDemoApplication83.class, args);
        }

    }

    @Configuration
    public class ApplicationContextBean {

        @Bean
        @LoadBalanced
        public RestTemplate getRestTemplate(){
            return new RestTemplate();
        }

    }

    @RestController
    public class NacosOrderController {

        @Resource
        private RestTemplate restTemplate;

        @Value("${service-url.nacos-user-serive}")
        private String serverUrl;

        @GetMapping(value = "/consumernacos/echo/{string}")
        public String echo(@PathVariable String string) {

            return restTemplate.getForObject(serverUrl+"/providernacos/echo/"+string,String.class)+"consumerHello Nacos Discovery " + string;
        }
    }
      