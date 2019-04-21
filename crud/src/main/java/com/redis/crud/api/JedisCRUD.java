package com.redis.crud.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * Jedis的CRUD操作
 */
@RestController
@RequestMapping(value = "/crud/jedis")
public class JedisCRUD {
    @Resource
    private JedisPool jedisPool;

    @ResponseBody
    @PostMapping("/jedisTest")
    public void jedisTest() {
        // 获取Jedis
        Jedis jedis = jedisPool.getResource();

        // 整数操作
        number(jedis);
        // List操作
        list(jedis);
        // Set操作
        set(jedis);
        // Hash操作
        hash(jedis);


        // 删除多个key
        jedis.del("test", "test1");

        // 分布式锁, 但setnx和expire不是原子操作, 要用lua脚本解决
        jedis.setnx("test", "100");
        jedis.expire("test", 60);
    }

    /**
     * 整数操作
     *
     * @param jedis jedis
     */
    private void number(Jedis jedis) {
        //向redis里存入数据
        jedis.set("test", "100");
        // 取数据
        jedis.get("test");
        // value + 1
        jedis.incr("test");
        // value - 1
        jedis.decr("test");
        // value + 2
        jedis.incrBy("test", 2L);
        // value - 2
        jedis.decrBy("test", 2L);
    }

    /**
     * List操作
     *
     * @param jedis jedis
     */
    private void list(Jedis jedis) {
        // 存list
        jedis.lpush("list", "value1", "value2", "value3");
        // 左插元素
        jedis.lpush("list", "value0");
        // 右插元素
        jedis.rpush("list", "value4");
        // 左删元素
        jedis.lpop("list");
        // 右删元素
        jedis.rpop("list");
        // 取[i,j]之间元素
        jedis.lrange("list", 1, 3);
        // 删除[i,j]之外的元素
        jedis.ltrim("list", 1, 3);
        // 删除指定元素val的个数num
        jedis.lrem("list", 1, "test3");
        // 获取指定下标元素
        jedis.lindex("list", 1);
        // 修改指定下标元素
        jedis.lset("list", 1, "aaa");
        // list长度
        jedis.llen("list");
        // 升序排序
        jedis.sort("list");
    }

    /**
     * Set操作
     *
     * @param jedis jedis
     */
    private void set(Jedis jedis) {
        // 存set
        jedis.sadd("set", "value1", "value2", "value3");
        // 取所有元素
        jedis.smembers("set");
        // 删除值为val1, val2的元素
        jedis.srem("set", "val1", "val2");
        // 随机取一个元素
        jedis.spop("set");
        // set元素个数
        jedis.scard("set");
        // 将val从set1剪切到set2
        jedis.smove("set1", "set2", "val");
        // set1,set2的交集
        jedis.sinter("set1", "set2");
        // set1,set2的并集
        jedis.sunion("set1", "set2");
        // set1,set2的差集
        jedis.sdiff("set1", "set2");
    }

    /**
     * Hash操作
     *
     * @param jedis jedis
     */
    private void hash(Jedis jedis) {
        // 存Hash
        jedis.hmset("hash", new HashMap<>());
        // 插入一个元素
        jedis.hmset("hash", new HashMap<>());
        // 获取所有元素
        jedis.hgetAll("hash");
        // 获取所有元素的key
        jedis.hkeys("hash");
        // 获取所有元素的value
        jedis.hvals("hash");
        // hash中k1对应的value - n
        jedis.hincrBy("hash", "k1", -2);
        // 删除hash中k1,k2元素
        jedis.hdel("hash", "k1", "k2");
        // hash中元素个数
        jedis.hlen("hash");
        // hash中是否存在k1
        jedis.hexists("hash", "k1");
        // hash中取k1,k2
        jedis.hmget("hash", "k1", "k2");
    }
}
