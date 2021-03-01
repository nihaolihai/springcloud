package com.demo.springcloud.utils;

import static com.demo.springcloud.utils.SendMessageUtil.getRandomCode;

public class TestSms {

    public static void main(String[] ares){
        Integer resultCode = SendMessageUtil.send("lvfaxxxxxx", "0b334927e1xxxxxxxxxx", "153xxxxxxxxx", "验证码:" + getRandomCode(6));
        System.out.println(SendMessageUtil.getMessage(resultCode));
    }
}
