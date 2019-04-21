package com.redis.cache.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.redis.base.domain.SysDict;

/**
 * @Auther: Akang
 * @Date: 2018/11/23 11:18
 * @Description:
 */
public interface SysDictService extends IService<SysDict> {
    /**
     * 查一个
     */
    SysDict queryById(Integer id);

    /**
     * 改一个
     */
    SysDict modifyById(SysDict sysDict);

    /**
     * 删一个
     */
    boolean dropById(Integer id);
}
