package com.demo.springcloud.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
