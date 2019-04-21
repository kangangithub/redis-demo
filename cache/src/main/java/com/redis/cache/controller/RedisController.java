package com.redis.cache.controller;

import com.redis.base.domain.SysDict;
import com.redis.cache.service.SysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

/**
 * @Auther: Akang
 * @Date: 2018/7/31 11:38
 * @Description:
 */
@RestController
@RequestMapping(value = "/cache/redis")
public class RedisController {

    /**
     * 一个接口多个实现, @Autowired, @Qualifier("beanID") 可以用@Resource代替
     * Spring创建bean时bean ID 默认类名首字母小写, 也可自定义
     */
    @Autowired
    @Qualifier("redis")
//    @Resource
    private SysDictService redis;

    /**
     * 查一个
     */
    @ResponseBody
    @PostMapping("/get/{id}")
    public SysDict getOne(@PathVariable(value = "id") int id) {
        return redis.queryById(id);
    }

    /**
     * 删一个
     */
    @ResponseBody
    @PostMapping("/drop/{id}")
    public boolean drop(@PathVariable(value = "id") int id) {
        return redis.dropById(id);
    }

    /**
     * 改一个
     */
    @ResponseBody
    @PostMapping("/modify/{id}")
    public SysDict modifyById(@PathVariable(value = "id") int id, @RequestBody SysDict sysDict) {
        return redis.modifyById(sysDict);
    }
}
