package com.rocketmq.cloud.youxia.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rocketmq.cloud.youxia.entity.GoodsSkuEntity;
import com.rocketmq.cloud.youxia.mapper.GoodsSkuMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
public class GoodsSkuManager {
    @Resource
    private GoodsSkuMapper goodsSkuMapper;
    public Integer insert(GoodsSkuEntity goodsSkuEntity){
        return goodsSkuMapper.insert(goodsSkuEntity);
    }

    //查询指定商品对应的SKU详细信息
    public GoodsSkuEntity selectEntityByGoodsSkuEntity(GoodsSkuEntity goodsSkuEntity){
        QueryWrapper<GoodsSkuEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("goods_id", goodsSkuEntity.getGoodsId());
        queryWrapper.eq("id", goodsSkuEntity.getId());
        queryWrapper.eq("properties", goodsSkuEntity.getProperties());
        queryWrapper.eq("title",goodsSkuEntity.getTitle());
        return goodsSkuMapper.selectOne(queryWrapper);
    }

    //返回指定商品对应的SKU库存
    public Integer selectEntityNumByGoodsSkuEntity(GoodsSkuEntity goodsSkuEntity) {
        QueryWrapper<GoodsSkuEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", goodsSkuEntity.getId());
        queryWrapper.eq("goods_id", goodsSkuEntity.getGoodsId());
        queryWrapper.eq("properties", goodsSkuEntity.getProperties());
        queryWrapper.eq("title", goodsSkuEntity.getTitle());
        GoodsSkuEntity dataFromDatabase=goodsSkuMapper.selectOne(queryWrapper);
        if(null!=dataFromDatabase){
            return dataFromDatabase.getNum();
        }
        return 0;
    }
}
