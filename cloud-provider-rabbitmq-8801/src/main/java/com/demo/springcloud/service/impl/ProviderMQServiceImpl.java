package com.demo.springcloud.service.impl;

import com.demo.springcloud.service.ProviderMQService;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

import javax.annotation.Resource;
import java.util.UUID;

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
