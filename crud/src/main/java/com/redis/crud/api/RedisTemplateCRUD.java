package com.redis.crud.api;

import org.springframework.data.redis.core.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * RedisTemplate的CRUD操作
 *
 * @Auther: Akang
 * @Date: 2018/7/31 14:19
 * @Description:
 */
@RestController
@RequestMapping(value = "/crud/redisTemplate")
public class RedisTemplateCRUD {
    /**
     * StringRedisTemplate继承RedisTemplate,两者区别: 采用的序列化方式不同
     * RedisTemplate使用的是 JdkSerializationRedisSerializer
     * StringRedisTemplate使用的是 StringRedisSerializer
     * 存取字符串类型，使用StringRedisTemplate，存取对象类型，使用RedisTemplate更好的选择。
     */
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedisTemplate redisTemplate;

    @ResponseBody
    @PostMapping("/stringRedisTemplateTest")
    public void stringRedisTemplateTest() {
        //向redis里存入数据和设置缓存时间  
        stringRedisTemplate.opsForValue().set("test", "100", 60 * 10, TimeUnit.SECONDS);
        //根据key获取缓存中的value
        stringRedisTemplate.opsForValue().get("test");

        //value -1
        stringRedisTemplate.boundValueOps("test").increment(-1);
        //value +1
        stringRedisTemplate.boundValueOps("test").increment(1);

        //根据key获取过期时间
        stringRedisTemplate.getExpire("test");
        //根据key获取过期时间并换算成指定单位
        stringRedisTemplate.getExpire("test", TimeUnit.SECONDS);

        //根据key删除缓存
        stringRedisTemplate.delete("test");

        //检查key是否存在，返回boolean值
        stringRedisTemplate.hasKey("546545");

        //设置过期时间
        stringRedisTemplate.expire("red_123", 1000, TimeUnit.MILLISECONDS);

        //向指定key中存放set集合
        stringRedisTemplate.opsForSet().add("red_123", "1", "2", "3");
        //根据key查看集合中是否存在指定数据
        stringRedisTemplate.opsForSet().isMember("red_123", "1");
        //根据key获取set集合
        stringRedisTemplate.opsForSet().members("red_123");
    }

    /**
     * redisTemplate.opsForValue(); 操作字符串
     * redisTemplate.opsForHash(); 操作hash
     * redisTemplate.opsForList(); 操作list
     * redisTemplate.opsForSet(); 操作set
     * redisTemplate.opsForZSet(); 操作有序set
     */
    @ResponseBody
    @PostMapping("/redisTemplateTest")
    public void redisTemplateTest() {
        opsForValue();
        opsForList();
        opsForHash();
        opsForSet();
        opsForZSet();
    }

    /**
     * redisTemplate.opsForZSet(); 操作有序set
     * Redis 有序集合和无序集合一样也是string类型元素的集合,且不允许重复的成员。
     * 不同的是每个元素都会关联一个double类型的分数。redis正是通过分数来为集合中的成员进行从小到大的排序。
     * 有序集合的成员是唯一的,但分数(score)却可以重复。
     */
    private void opsForZSet() {
        //新增一个有序集合，存在的话为false，不存在的话为true
        redisTemplate.opsForZSet().add("zset1", "zset-1", 1.0); // true
        //如果存在,则更新, 不存在,新增   元组(Tuple)
        redisTemplate.opsForZSet().add("zset2", new HashSet<ZSetOperations.TypedTuple<Object>>() {{
            add(new DefaultTypedTuple("value1", 9.1));
            add(new DefaultTypedTuple("value2", 9.2));
            add(new DefaultTypedTuple("value3", 9.3));
            add(new DefaultTypedTuple("value4", 9.4));
        }});
        //删除ZSet的一个或多个元素
        redisTemplate.opsForZSet().remove("zset2", new Object[]{"value3", "value4"}); // zset2: {value1=9.1, value2=9.2}
        //增加元素的score值，并返回增加后的值
        redisTemplate.opsForZSet().incrementScore("zset2", "value2", 0.1); // 9.3 原为9.2

        /**
         * zset2: {value1=9.1, value2=9.2, value3=9.3, value4=9.4}
         * count 两个边界值都包含
         */
        //返回ZSet中[9.0, 9.3]Score区中元素个数，score升序
        redisTemplate.opsForZSet().count("zset2", 9.0, 9.3);
        //返回ZSet中元素个数, size()调用了zCard()方法
        redisTemplate.opsForZSet().size("zset2"); // 4
        redisTemplate.opsForZSet().zCard("zset2"); // 4
        //返回ZSet中value1的score
        redisTemplate.opsForZSet().score("zset2", "value1"); // 9.1
        //移除value[1,2]区间的元素, score升序
        redisTemplate.opsForZSet().removeRange("zset2", 1, 2);
        //移除score[9.1,9.2]区间的元素, score升序
        redisTemplate.opsForZSet().removeRangeByScore("zset2", 9.1, 9.2);

        /**
         * zset1: {value1=0.1, value2=0.2, value5=9.5, value6=9.6}
         * zset2: {value1=9.1, value2=9.2, value3=9.3, value4=9.4}
         * zset3: {value1=0.1, value2=0.2, value7=9.7, value8=9.8}
         *
         * ***AndStore 返回元组Tuple的集合
         * 并集
         */
        //返回的zset1, zset2并集并保存在zset4中，key相同的话会把score值相加
        redisTemplate.opsForZSet().unionAndStore("zset1", "zset2", "zset4"); // zset4: {value1=9.2, value2=9.4, value3=9.3, value4=9.4, value5=9.5, value6=9.6}
        //返回的zset1, zset2, zzset3并集并保存在zset5中，key相同的话会把score值相加
        redisTemplate.opsForZSet().unionAndStore("zset1", new ArrayList<String>() {{
            add("zset2");
            add("zset3");
        }}, "zset5"); // zset5: {value1=9.3, value2=9.6, value3=9.3, value4=9.4, value5=9.5, value6=9.6, value7=9.7, value8=9.8}

        /**
         * 交集
         */
        //返回的zset1, zset2交集并保存在zset6中，key相同的话会把score值相加
        redisTemplate.opsForZSet().intersectAndStore("zset1", "zset2", "zset6"); // zset6: {value1=9.2, value2=9.4}
        //返回的zset1, zset2, zzset3交集并保存在zset7中，key相同的话会把score值相加
        redisTemplate.opsForZSet().intersectAndStore("zset1", new ArrayList<String>() {{
            add("zset2");
            add("zset3");
        }}, "zset7"); // zset7: {value1=9.3, value2=9.6}

        //使用Cursor在key的ZSet中迭代，相当于迭代器
        Cursor<ZSetOperations.TypedTuple<Object>> cursor = redisTemplate.opsForZSet().scan("zzset1", ScanOptions.NONE);
        while (cursor.hasNext()){
            ZSetOperations.TypedTuple<Object> item = cursor.next();
            System.out.println(item.getValue() + ":" + item.getScore());
        } // zset1: {value1:0.1, value2:0.2, value5:9.5, value6:9.6}


        /**
         * zset2: {value1=9.1, value2=9.2, value3=9.3, value4=9.4}
         * range, rangeWithScores, rangeByScore, rangeByScoreWithScores ... 两个边界值都包含
         * xxxWithScores() 返回元组Tuple的集合
         * score升序
         */
        //返回ZSet中value2的排名，score升序
        redisTemplate.opsForZSet().rank("zset2", "value2"); // 2
        //返回ZSet中[0, 1]区间内的value，score升序 -1代表取所有
        redisTemplate.opsForZSet().range("zset2", 0, 1); // value1, value2
        //返回ZSet中[0, 长度]内的value及score，score升序, -1代表所有
        redisTemplate.opsForZSet().rangeWithScores("zset2", 0, -1); // value:value1 score:9.1   value:value2 score:9.2  value:value3 score:9.3   value:value4 score:9.4
        //返回ZSet中[9.0, 9.3]的Score区间内的value，score升序
        redisTemplate.opsForZSet().rangeByScore("zset2", 9.0, 9.3); // value1 value2 value3
        //返回ZSet中[9.0, 9.3]的Score区间内的value及Score，score升序
        redisTemplate.opsForZSet().rangeByScoreWithScores("zset2", 9.0, 9.3); // value:value1 score:9.1   value:value2 score:9.3
        //返回ZSet中[9.0, 9.3]Score区间及[1, 2]排序区间内的value，score升序
        redisTemplate.opsForZSet().rangeByScore("zset2", 9.0, 9.3, 1, 2); // value1 value2
        //返回ZSet中[9.0, 9.3]Score区间及[1, 2]排序区间内的value及score，score升序
        redisTemplate.opsForZSet().rangeByScoreWithScores("zset2", 9.0, 9.3, 1, 2); // value:value1 score:9.1   value:value2 score:9.2

        /**
         * score降序
         */
        //返回ZSet中指定成员的排名，score降序
        redisTemplate.opsForZSet().reverseRank("zset2", "value2"); // 3
        //返回ZSet中[1,2]区间内的value, score降序
        redisTemplate.opsForZSet().reverseRange("zset2", 1, 2); // value4 value3
        //返回ZSet中[1,2]区间内的value及score, score降序
        redisTemplate.opsForZSet().reverseRangeWithScores("zset2", 1, 2); // value:value4 score:9.4  value:value3 score:9.3
        //返回ZSet中[9.0, 9.3]的Score区间内的value，score降序
        redisTemplate.opsForZSet().reverseRangeByScore("zset2", 9.0, 9.3); // value3
        //返回ZSet中[9.0, 9.3]的Score区间内的value及score，score降序
        redisTemplate.opsForZSet().reverseRangeByScoreWithScores("zset2", 9.0, 9.3); // value:value3 score:9.3
        //返回ZSet中[9.0, 9.3]Score区间及[1, 2]排序区间内的value, score降序
        redisTemplate.opsForZSet().reverseRangeByScore("zset2", 9.0, 9.3, 1, 2); // value3
        //返回ZSet中[9.0, 9.3]Score区间及[1, 2]排序区间内的value及score, score降序
        redisTemplate.opsForZSet().reverseRangeByScoreWithScores("zset2", 9.0, 9.3, 1, 2); // value:value3 score:9.3
    }

    /**
     * redisTemplate.opsForSet();操作set
     * Redis的Set是string类型的无序集合。集合成员是唯一的，这就意味着集合中不能出现重复的数据。
     * Redis中集合是通过哈希表实现的，所以添加，删除，查找的复杂度都是O(1)。
     */
    private void opsForSet() {
        /**
         * setTest2: [str3, str4]
         */
        //添加元素，返回添加个数
        redisTemplate.opsForSet().add("setTest", new String[]{"str1", "str2", "str3", "str4", "str5"}); // 5
        //移除集合中一个或多个成员, 返回删除元素的个数
        redisTemplate.opsForSet().remove("setTest", new String[]{"str4", "str5"}); // 2   setTest: [str1, str2, str3]
        //移除并返回集合中的一个随机元素
        redisTemplate.opsForSet().pop("setTest"); // str1
        //将str2从setTest移动到setTest2
        redisTemplate.opsForSet().move("setTest", "str2", "setTest2"); // setTest: [str3]   setTest2: [str2, str3, str4]
        //获取set长度
        redisTemplate.opsForSet().size("setTest"); // 1
        //是否set的元素
        redisTemplate.opsForSet().isMember("setTest", "str6"); // false
        //使用Cursor在key的set中迭代，相当于迭代器
        Cursor<Object> cursor = redisTemplate.opsForSet().scan("setTest", ScanOptions.NONE);
        while (cursor.hasNext()) {
            System.out.println(cursor.next());
        } // str3

        /**
         * 交集 setTest: [str1, str2, str3], setTest2: [str2, str3], setTest3: [str3, str4]
         */
        //求setTest与setTest2的交集
        redisTemplate.opsForSet().intersect("setTest", "setTest2"); // [str2, str3]
        //求setTest与setTest2的交集并存入setTest4
        redisTemplate.opsForSet().intersectAndStore("setTest", "setTest2", "setTest4"); // setTest4: [str2, str3]

        //求setTest与setTest2, setTest3的交集
        redisTemplate.opsForSet().intersect("setTest", new ArrayList<String>() {{
            add("setTest2");
            add("setTest3");
        }}); // [str3]
        //求setTest与setTest2, setTest3的交集并存入setTest5
        redisTemplate.opsForSet().intersectAndStore("setTest", new ArrayList<String>() {{
            add("setTest2");
            add("setTest3");
        }}, "setTest5"); // setTest5: [str3]

        /**
         * 并集 setTest: [str1, str2, str3], setTest2: [str2, str3], setTest3: [str3, str4]
         */
        //求setTest与setTest2的并集
        redisTemplate.opsForSet().union("setTest", "setTest2"); // [str1, str2, str3]
        //求setTest与setTest2的并集并存入setTest6
        redisTemplate.opsForSet().unionAndStore("setTest", "setTest2", "setTest6"); // setTest6: [str1, str2, str3]
        //求setTest与setTest2, setTest3的并集
        redisTemplate.opsForSet().union("setTest", new ArrayList<String>() {{
            add("setTest2");
            add("setTest3");
        }}); // [str1, str2, str3, str4]
        //求setTest与setTest2, setTest3()的并集并存入setTest7
        redisTemplate.opsForSet().unionAndStore("setTest", new ArrayList<String>() {{
            add("setTest2");
            add("setTest3");
        }}, "setTest7"); // setTest7: [str1, str2, str3, str4]

        /**
         * 差集 setTest: [str1, str2, str3], setTest2: [str2, str3], setTest3: [str3, str4]
         */
        //求setTest与setTest2的差集, 以setTest为基础
        redisTemplate.opsForSet().difference("setTest", "setTest2"); // [str1]
        //求setTest与setTest2的差集并存入setTest8
        redisTemplate.opsForSet().differenceAndStore("setTest", "setTest2", "setTest8"); // setTest8: [str1]
        //求setTest与setTest2, setTest3的差集
        redisTemplate.opsForSet().difference("setTest", new ArrayList<String>() {{
            add("setTest2");
            add("setTest3");
        }}); // [str1]
        //求setTest与setTest2, setTest3的差集并存入setTest9
        redisTemplate.opsForSet().differenceAndStore("setTest", new ArrayList<String>() {{
            add("setTest2");
            add("setTest3");
        }}, "setTest9"); // setTest9: [str1]

        /**
         * 获取Set元素 setTest: [str1, str2, str3, str4]
         */
        //获取Set所有元素
        redisTemplate.opsForSet().members("setTest"); // [str1, str2, str3, str4]
        //随机获取set中的1个元素
        redisTemplate.opsForSet().randomMember("setTest"); // [str3]
        //随机获取set中的3个元素
        redisTemplate.opsForSet().randomMembers("setTest", 3); // [str1, str2, str2]
        //随机获取set中的5个元素(不重复取)
        redisTemplate.opsForSet().distinctRandomMembers("setTest", 3); // [str1, str2, str3]
    }

    /**
     * redisTemplate.opsForHash();操作hash
     * Redis的散列可以让用户将多个键值对存储到一个Redis键里面。
     */
    private void opsForHash() {
        //插入
        redisTemplate.opsForHash().put("redisHash", "name", "tom");
        redisTemplate.opsForHash().putAll("redisHash1", new HashMap<String, Object>() {{
            put("name", "jack");
            put("age", 27);
            put("class", "1");
            put("sex", "M");
        }});
        //删除
        redisTemplate.opsForHash().delete("redisHash1", "sex");
        //获取hash
        redisTemplate.opsForHash().entries("redisHash1"); // {name=jack, class=1, age=27}
        //判断key是否存在
        redisTemplate.opsForHash().hasKey("redisHash1", "age"); // true
        //获取hash的value
        redisTemplate.opsForHash().get("redisHash1", "age"); // 27
        //获取hash中多个的value
        redisTemplate.opsForHash().multiGet("redisHash1", new ArrayList<Object>() {{
            add("name");
            add("age");
        }}); // [jack, 27]
        //hash的value +1 / -1.1, delta支持浮点值
        redisTemplate.opsForHash().increment("redisHash1", "age", 1); // age=28
        redisTemplate.opsForHash().increment("redisHash1", "age", -1.1); // age=26.9
        //获取hash所有key
        redisTemplate.opsForHash().keys("redisHash1"); // [name, class, age]
        //获取hash长度
        redisTemplate.opsForHash().size("redisHash1"); // 3
        //仅当hashKey不存在时才设置散列hashKey的值
        redisTemplate.opsForHash().putIfAbsent("redisHash1", "age", 27); // false  age=26.9
        redisTemplate.opsForHash().putIfAbsent("redisHash1", "kkk", "kkk"); // true  kkk=kkk
        //获取hash所有key
        redisTemplate.opsForHash().values("redisHash1"); // {jack, 1, 26.9, kkk}
        //使用Cursor在key的hash中迭代，相当于迭代器
        Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan("redisHash", ScanOptions.NONE);
        while (cursor.hasNext()) {
            Map.Entry<Object, Object> entry = cursor.next();
            System.out.println(entry.getKey() + ":" + entry.getValue());
        } // name:jack  age:26.9  class:1 kkk:kkk
    }

    /**
     * redisTemplate.opsForList();操作list
     */
    private void opsForList() {
        /**
         * list : [c#, c++, python, java, c#, c#]   -1 代表取所有
         */
        //返回[0, 所有]区间内的value, -1 代表取所有
        redisTemplate.opsForList().range("list", 0, -1); // [c#, c++, python, java, c#, c#]
        //截取[1,2]区间内的value
        redisTemplate.opsForList().trim("list", 1, 2);
        //返回list的长度
        redisTemplate.opsForList().size("list"); // 6
        //设置列表index位置的值
        redisTemplate.opsForList().set("list", 1, "php");


        /**
         * list : [c#, c++, python, java]
         * 从左边插入元素
         */
        //从左边插入1个元素,返回列表长度
        redisTemplate.opsForList().leftPush("list", "java"); // 5
        //如果pivot值存在,从pivot位置左边插入,返回列表长度, 如果没有找到指定元素 ，返回 -1 。 如果 key 不存在或为空列表，返回 0
        redisTemplate.opsForList().leftPush("list", "java", "oc"); // 5
        //从左边插入n个元素,返回列表长度
        redisTemplate.opsForList().leftPushAll("list", new String[]{"1", "2", "3"}); // 7
        //list存在, 插入元素并返回list长度, list不存在, 插入失败返回0
        redisTemplate.opsForList().leftPushIfPresent("list", "c++"); // 5

        /**
         * list : [c#, c++, python, java]
         * 从右边插入元素
         */
        //从右边插入1个元素,返回列表长度
        redisTemplate.opsForList().rightPush("list", "java"); // 5
        //如果pivot值存在,从pivot位置右边插入,返回列表长度, 如果没有找到指定元素 ，返回 -1 。 如果 key 不存在或为空列表，返回 0
        redisTemplate.opsForList().rightPush("list", "java", "oc"); // 5
        //从右边插入n个元素,返回列表长度
        redisTemplate.opsForList().rightPushAll("list", new String[]{"1", "2", "3"}); // 7
        //list存在, 插入元素并返回list长度, list不存在, 插入失败返回0
        redisTemplate.opsForList().rightPushIfPresent("list", "c++"); // 5

        /**
         * list : [c#, c++, python, java, c#, php]
         *
         * 删除列表中第一个value
         * count > 0：删除等于从头到尾开始。
         * count < 0：删除等于从尾到头开始。
         * count = 0：删除等于value的所有元素。
         */
        redisTemplate.opsForList().remove("list", 1, "c#"); // [c++, python, java, c#, php]
        redisTemplate.opsForList().remove("list", -1, "c#"); // [c#, c++, python, java, php]
        redisTemplate.opsForList().remove("list", 0, "c#"); // [c++, python, java, php]
        //返回列表index位置的元素
        redisTemplate.opsForList().index("list", 2); // c++

        /**
         * list : [c#, c++, python, java]
         *
         * V leftPop(K key);
         * 弹出最左边的元素，弹出之后该值在列表中将不复存在
         * V leftPop(K key, long timeout, TimeUnit unit);
         * 移出并获取列表的第一个元素，如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
         * V rightPop(K key);
         * 弹出最右边的元素，弹出之后该值在列表中将不复存在
         * V rightPop(K key, long timeout, TimeUnit unit);
         * 移出并获取列表的最右边一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
         * V rightPopAndLeftPush(K sourceKey, K destinationKey);
         * 用于移除列表的最右边一个元素，并将该元素添加到另一个列表并返回。
         * V rightPopAndLeftPush(K sourceKey, K destinationKey, long timeout, TimeUnit unit);
         * 用于移除列表的最右边一个元素，并将该元素添加到另一个列表并返回，如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
         */
        redisTemplate.opsForList().leftPop("list"); // c#
        redisTemplate.opsForList().rightPop("list"); // java
        redisTemplate.opsForList().rightPopAndLeftPush("list", "list3"); // c#  list3: [c#]
    }

    /**
     * opsForValue 操作字符串,建议用StringRedisTemplate
     */
    private void opsForValue() {

        redisTemplate.opsForValue().set("name", "tom");
        redisTemplate.opsForValue().get("name");  //tom
        redisTemplate.opsForValue().getAndSet("getSetTest", "test2"); //test2
        redisTemplate.opsForValue().size("appendTest"); //5 value长度
        redisTemplate.opsForValue().get("appendTest", 0, 3); //tes

        //设置有效时间
        redisTemplate.opsForValue().set("name", "tom", 10, TimeUnit.SECONDS);
        redisTemplate.opsForValue().get("name"); //由于设置的是10秒失效，十秒之内查询有结果，十秒之后返回为null

        //用 value 参数覆写(overwrite)给定 key 所储存的字符串值，从偏移量 offset 开始
        redisTemplate.opsForValue().set("key", "hello world");
        redisTemplate.opsForValue().set("key", "redis", 6); //结果：hello redis

        redisTemplate.opsForValue().setIfAbsent("multi1", "multi1");//false说明multi1已存在 true说明不存在

        //存取多个
        redisTemplate.opsForValue().multiSet(new HashMap<String, String>() {
            {
                put("multi1", "multi1");
                put("multi2", "multi2");
                put("multi3", "multi3");
            }
        });
        redisTemplate.opsForValue().multiGet(new ArrayList<String>() {
            {
                add("multi1");
                add("multi2");
                add("multi3");
            }
        });

        //为多个键分别设置值，如果存在则返回false，不存在返回true
        redisTemplate.opsForValue().multiSetIfAbsent(new HashMap<String, String>() {
            {
                put("multi1", "multi1");
                put("multi2", "multi2");
                put("multi3", "multi3");
            }
        });

        //value +1.2  value -1.2
        redisTemplate.opsForValue().increment("increlong", 1.2);
        redisTemplate.opsForValue().increment("increlong", -1.2);

        //追加, 如果key存在并且是字符串，则追加到字符串的末尾。如果key不存在，则它被创建并设置为空字符串
        redisTemplate.opsForValue().append("appendTest", "Hello"); //Hello
        redisTemplate.opsForValue().append("appendTest", "world"); //Helloworld

        //对 key 所储存的字符串值，设置或清除指定偏移量上的位(bit)
        redisTemplate.opsForValue().set("bitTest", "a");
        // 'a' 的ASCII码是 97。转换为二进制是：01100001
        // 'b' 的ASCII码是 98  转换为二进制是：01100010
        // 'c' 的ASCII码是 99  转换为二进制是：01100011
        //因为二进制只有0和1，在setBit中true为1，false为0，因此我要变为'b'的话第六位设置为1，第七位设置为0
        redisTemplate.opsForValue().setBit("bitTest", 6, true);
        redisTemplate.opsForValue().setBit("bitTest", 7, false);
        redisTemplate.opsForValue().get("bitTest"); // b
    }

}
