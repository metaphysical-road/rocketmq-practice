package com.rocketmq.cloud.youxia.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rocketmq.cloud.youxia.entity.BrandEntity;
import com.rocketmq.cloud.youxia.entity.GoodEntity;
import com.rocketmq.cloud.youxia.mapper.GoodMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class GoodManager {
    @Resource
    private GoodMapper goodMapper;

    public Integer insert(GoodEntity goodEntity){
        return goodMapper.insert(goodEntity);
    }

    public GoodEntity selectGoodByName(String goodName){
        QueryWrapper<GoodEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("goods_name", goodName);
        return goodMapper.selectOne(queryWrapper);
    }
}
