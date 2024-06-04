package com.rocketmq.cloud.youxia.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rocketmq.cloud.youxia.entity.GoodsPropertyEntity;
import com.rocketmq.cloud.youxia.entity.PropertyNameEntity;
import com.rocketmq.cloud.youxia.mapper.GoodPropertyMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
public class GoodPropertyManager {
    @Resource
    private GoodPropertyMapper goodPropertyMapper;

    public Integer insert(GoodsPropertyEntity goodsPropertyEntity){
        return goodPropertyMapper.insert(goodsPropertyEntity);
    }

    public Integer selectNumByEntity(GoodsPropertyEntity goodsPropertyEntity){
        QueryWrapper<GoodsPropertyEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("goods_id", goodsPropertyEntity.getGoodsId());
        queryWrapper.eq("prop_name_id", goodsPropertyEntity.getPropNameId());
        queryWrapper.eq("prop_value_id", goodsPropertyEntity.getPropValueId());
        return goodPropertyMapper.selectCount(queryWrapper);
    }
}
