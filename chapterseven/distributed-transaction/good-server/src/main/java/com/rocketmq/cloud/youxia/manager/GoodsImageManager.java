package com.rocketmq.cloud.youxia.manager;

import com.rocketmq.cloud.youxia.entity.GoodsImageEntity;
import com.rocketmq.cloud.youxia.mapper.GoodsImageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class GoodsImageManager {
    @Resource
    private GoodsImageMapper goodsImageMapper;

    public Integer insert(GoodsImageEntity goodsImageEntity){
        return goodsImageMapper.insert(goodsImageEntity);
    }
}
