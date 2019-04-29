package com.redis.crud.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.SortingParams;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * Jedis的CRUD操作
 */
@RestController
@RequestMapping(value = "/crud/jedis")
public class JedisCRUD {
//    @Resource
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
        // ZSet操作
        zSet(jedis);
        // sort操作
        sort(jedis);
        // 分布式锁
        distributedLock(jedis, "lockKey", "requestId", "60");
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
        // 删除多个key
        jedis.del("test", "test1");
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

    /**
     * ZSet操作
     *
     * @param jedis jedis
     */
    private void zSet(Jedis jedis) {
        // 添加
        jedis.zadd("zset", 0.1D, "1");
        jedis.zadd("zset", new HashMap<String, Double>() {{
            put("1", 0.1D);
        }});
        // 取下标[2,4]元素
        jedis.zrange("zset", 2, 4);
        // 取下标[2,4]元素和score
        jedis.zrangeWithScores("zset", 2, 4);
        // 取score[0.2,0.4]元素
        jedis.zrangeByScore("zset", 0.2, 0.4);
        // 取score[0.2,0.4]元素和score
        jedis.zrangeByScoreWithScores("zset", 0.2, 0.4);
        // 取值为val的score
        jedis.zscore("zset", "val");
        // 取值为val的排名
        jedis.zrank("zset", "val");
        // 删值为val的元素
        jedis.zrem("zset", "val");
        // 取元素个数
        jedis.zcard("zset");
        // 取score[0.2,0.4]元素个数
        jedis.zcount("zset", 0.2, 0.4);
        // 值为val的score+1
        jedis.zincrby("zset", 1, "val");
    }

    /**
     * sort操作
     *
     * @param jedis jedis
     */
    private void sort(Jedis jedis) {
        // 生成排序对象
//        new SortParameters();
        // a-z排序
        jedis.sort("key", new SortingParams().alpha());
        // 升序
        jedis.sort("key", new SortingParams().asc());
        //降序
        jedis.sort("key", new SortingParams().desc());
    }

    private static final String LOCK_SUCCESS = "OK";
    private static final Long RELEASE_SUCCESS = 1L;

    /**
     * 分布式锁:LUA脚本
     *
     * @param jedis      Redis客户端
     * @param lockKey    锁
     * @param requestId  请求标识
     * @param expireTime 超期时间
     */
    private void distributedLock(Jedis jedis, String lockKey, String requestId, String expireTime) {
        tryGetDistributedLock(jedis, lockKey, requestId, expireTime);
        releaseDistributedLock(jedis, lockKey, requestId);
    }

    /**
     * 尝试获取分布式锁
     *
     * @param jedis      Redis客户端
     * @param lockKey    锁
     * @param requestId  请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
     */
    private static boolean tryGetDistributedLock(Jedis jedis, String lockKey, String requestId, String expireTime) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, 3, lockKey, requestId, expireTime);
        return LOCK_SUCCESS.equals(result);
    }

    /**
     * 释放分布式锁
     *
     * @param jedis     Redis客户端
     * @param lockKey   锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    private static boolean releaseDistributedLock(Jedis jedis, String lockKey, String requestId) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, 2, lockKey, requestId);
        return RELEASE_SUCCESS.equals(result);
    }
}
