package com.redis.consumer;

import org.springframework.stereotype.Component;

/**
 * @Auther: Akang
 * @Date: 2018/8/1 17:15
 * @Description:
 */
@Component
public class MessageReceiver {

    /**
     * 接收消息的方法
     */
    public void receiveMessage(String message) {
        System.out.println("收到一条消息：" + message);
    }

}