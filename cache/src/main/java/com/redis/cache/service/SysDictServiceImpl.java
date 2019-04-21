package com.redis.cache.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.redis.base.domain.SysDict;
import com.redis.cache.dao.SysDictMapper;
import org.springframework.stereotype.Service;

/**
 * @Auther: Akang
 * @Date: 2018/11/23 14:59
 * @Description:
 */
@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService {

    @Override
    public SysDict queryById(Integer id) {
        return null;
    }

    @Override
    public SysDict modifyById(SysDict sysDict) {
        return null;
    }

    @Override
    public boolean dropById(Integer id) {
        return false;
    }
}
