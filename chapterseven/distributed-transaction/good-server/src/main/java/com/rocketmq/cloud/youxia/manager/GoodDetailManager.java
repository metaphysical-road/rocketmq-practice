package com.rocketmq.cloud.youxia.manager;

import com.rocketmq.cloud.youxia.entity.GoodDetailEntity;
import com.rocketmq.cloud.youxia.mapper.GoodDetailMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class GoodDetailManager {
    @Resource
    private GoodDetailMapper goodDetailMapper;

    public Integer insert(GoodDetailEntity goodDetailEntity){
        return goodDetailMapper.insert(goodDetailEntity);
    }
}
