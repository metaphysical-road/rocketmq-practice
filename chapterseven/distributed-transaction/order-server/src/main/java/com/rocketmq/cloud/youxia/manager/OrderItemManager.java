package com.rocketmq.cloud.youxia.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rocketmq.cloud.youxia.entity.OrderEntity;
import com.rocketmq.cloud.youxia.entity.OrderItemEntity;
import com.rocketmq.cloud.youxia.mapper.OrderItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderItemManager {

    @Autowired
    private OrderItemMapper orderItemMapper;

    public Integer insert(OrderItemEntity orderItemEntity){
        return orderItemMapper.insert(orderItemEntity);
    }

    public OrderItemEntity selectByOrderItemEntity(OrderItemEntity orderItemEntity) {
        QueryWrapper<OrderItemEntity> queryWrapper = new QueryWrapper<>();
        if (null != orderItemEntity.getOrderSn()) {
            queryWrapper.eq("trade_sn", orderItemEntity.getTradeSn());
        } else if (null != orderItemEntity.getSn()) {
            queryWrapper.eq("sn", orderItemEntity.getSn());
        } else if (null != orderItemEntity.getId()) {
            queryWrapper.eq("id", orderItemEntity.getId());
        }
        return orderItemMapper.selectOne(queryWrapper);
    }

    public Integer selectNumByOrderEntity(OrderItemEntity orderItemEntity) {
        QueryWrapper<OrderItemEntity> queryWrapper = new QueryWrapper<>();
        if (null != orderItemEntity.getTradeSn()) {
            queryWrapper.eq("trade_sn", orderItemEntity.getTradeSn());
        } else if (null != orderItemEntity.getSn()) {
            queryWrapper.eq("sn", orderItemEntity.getSn());
        } else if (null != orderItemEntity.getId()) {
            queryWrapper.eq("id", orderItemEntity.getId());
        }
        return orderItemMapper.selectCount(queryWrapper);
    }
}
