package com.redis.cache.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.redis.cache.dao.SysDictMapper;
import com.redis.base.domain.SysDict;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
/**
 * 统一配置本类的缓存注解的属性
 */
@CacheConfig(cacheNames = "Redis")
@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
public class Redis extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService {

    /**
     * @AliasFor 给注解的属性起别名
     * @Cacheable, @CachePut, @CacheEvict 的value和cacheNames属性一样, 互为别名
     * @Cacheable 标注到读取数据的方法上，先查询是否已经有缓存，有会使用缓存，没有则会执行方法并缓存。
     * value: 指定缓存存放在哪块命名空间。
     * key: 缓存key，如果不指定将使用默认的KeyGenerator生成
     * condition: 满足缓存条件的数据才会从缓存中读取，或者在不存在的时候添加到缓存中
     * unless: 表示条件表达式成立的话不放入缓存, #result:返回值, 该表达只在方法执行之后判断，此时可以拿到返回值result进行判断
     * 注: eq =,  ne !=, gt >, lt <, ge >=, le <=
     *
     * redis命令:
     * get value::key --> get SysDict::id:110
     *
     * 查询结果不为null,缓存查询结果 set SysDict::id:110 SysDict
     */
    @Override
    @Cacheable(value = "SysDict", key = "'id:' + #id", condition = "#id > 100", unless = "#result eq null")
    public SysDict queryById(Integer id) {
        return getById(id);
    }

    /**
     * @CachePut 标注到写数据的方法上，如新增/修改方法，调用方法时会自动把符合条件的数据存入缓存, 每次都会触发真实方法的调用,
     * 该注解的value 和 key 必须与要与@Cacheable相同
     * value: 指定缓存存放在哪块命名空间。
     * key: 缓存key，如果不指定将使用默认的KeyGenerator生成
     * condition: 满足缓存条件的数据才会放入缓存，CachePut的condition只在调用方法之后判断，因此可以得到result
     * unless: 用于否决缓存更新的，只在方法执行之后判断，此时可以拿到返回值result进行判断
     *
     * 不论更新成功/失败,都会更新缓存, set SysDict::id:110 SysDict
     */
    @Override
    @CachePut(value = "SysDict", key = "'id:' + #sysDict.id")
    public SysDict modifyById(SysDict sysDict) {
        return updateById(sysDict) ? sysDict : getById(sysDict.getId());
    }

    /**
     * @CacheEvict: 标注到移除数据的方法上，如删除方法，调用方法时会从缓存中移除符合条件的数据
     * value: 指定缓存存放在哪块命名空间。
     * key: 缓存key，如果不指定将使用默认的KeyGenerator生成
     * condition: 满足缓存条件的数据才会从缓存中移除，condition在调用方法之前和之后都会判断
     * allEntries: 是否移除所有数据
     * beforeInvocation: 是调用方法之前移除/还是调用之后移除
     *
     * 清空缓存: del SysDict::id:110 SysDict
     */
    @Override
    @CacheEvict(value = "SysDict", key = "'id:' + #id", condition = "#result eq true")
    public boolean dropById(Integer id) {
        return removeById(id);
    }
}
