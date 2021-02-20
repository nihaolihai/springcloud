package com.demo.springcloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(Sink.class)//定义消息的消费管道
public class ProviderMQController {

    @Value("${server.port}")
    private String serverport;

    @StreamListener(Sink.INPUT)
    public void receive(Message<String> message){
        System.out.println("这是第已通道接受值为："+message.getPayload());
    }
}
