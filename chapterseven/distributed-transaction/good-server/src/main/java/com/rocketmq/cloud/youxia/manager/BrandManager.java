package com.rocketmq.cloud.youxia.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rocketmq.cloud.youxia.entity.BrandEntity;
import com.rocketmq.cloud.youxia.entity.CategoryEntity;
import com.rocketmq.cloud.youxia.mapper.BrandMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class BrandManager {
    @Resource
    private BrandMapper brandMapper;

    public Integer insert(BrandEntity brandEntity){
        return brandMapper.insert(brandEntity);
    }

    public BrandEntity select(String brandName){
        QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", brandName);
        return brandMapper.selectOne(queryWrapper);
    }
}

