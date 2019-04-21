package com.redis.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Auther: Akang
 * @Date: 2018/8/1 17:14
 * @Description:
 */
//开启定时器功能
//@EnableScheduling
@Component
public class MessageSender {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 项目启动会间隔2s自动通过StringRedisTemplate对象向redis消息队列chat频道发布消息
     */
//    @Scheduled(fixedRate = 2000)
    public void sendMessage() {
        stringRedisTemplate.convertAndSend("chat", String.valueOf(Math.random()));
    }
}