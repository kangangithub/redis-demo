package com.redis.crud.demo;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * 抢红包
 */
@RestController
@RequestMapping(value = "/crud/grabRedPacket")
public class GrabRedPacket {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 初始化红包列表至redis
     */
    @PostMapping("/initRedPacket")
    public void initRedPacket() {
        // 红包并存入Redis
        redisTemplate.opsForList().leftPushAll("redPacket", "1", "3", "2.5", "5", "1.2", "3.3", "4.5", "7.8", "1.9", "2.7");
        System.out.println(redisTemplate.opsForList().range("redPacket", 0, -1));
    }

    /**
     * 删除红包列表
     */
    @PostMapping("/delRedPacket")
    public void delRedPacket() {
        redisTemplate.delete("redPacket");
    }

    /**
     * 多用户并发抢红包的情况且避免出现重复领红包
     */
    @PostMapping("/testGrabRedPacket")
    public void testGrabRedPacket() {
        // 抢红包
        Object pop = redisTemplate.opsForList().leftPop("redPacket");
        if (pop != null) {
            // 将用户ID存入set,UUID模拟用户ID
            Long add = redisTemplate.opsForSet().add("userId", UUID.randomUUID().toString());
            if (add == 1L) {
                // 存储成功说明该用户是第一次抢到红包
                System.out.println(Thread.currentThread().getName() + "抢到了" + pop + "的红包");
            } else {
                // 存储成功说明该用户不是第一次抢到红包,不允许一个用户枪多个红包,把抢到的红包再插回红包列表
                redisTemplate.opsForList().rightPush("redPacket", pop);
            }
        } else {
            // 返回空说明红包列表为空,红包已被抢光
            System.out.println("红包已经被抢完," + Thread.currentThread().getName() + "没有抢到红包");
        }
    }
}
