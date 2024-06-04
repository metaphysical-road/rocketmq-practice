package com.rocketmq.cloud.youxia.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rocketmq.cloud.youxia.entity.OrderEntity;
import com.rocketmq.cloud.youxia.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderManager {

    @Autowired
    private OrderMapper orderMapper;

    public Integer insert(OrderEntity orderEntity){
        return orderMapper.insert(orderEntity);
    }

    public OrderEntity selectByOrderEntity(OrderEntity orderEntity) {
        QueryWrapper<OrderEntity> queryWrapper = new QueryWrapper<>();
        if (null != orderEntity.getTradeSn()) {
            queryWrapper.eq("trade_sn", orderEntity.getTradeSn());
        } else if (null != orderEntity.getSn()) {
            queryWrapper.eq("sn", orderEntity.getSn());
        } else if (null != orderEntity.getId()) {
            queryWrapper.eq("id", orderEntity.getId());
        }
        return orderMapper.selectOne(queryWrapper);
    }

    public Integer selectNumByOrderEntity(OrderEntity orderEntity) {
        QueryWrapper<OrderEntity> queryWrapper = new QueryWrapper<>();
        if (null != orderEntity.getTradeSn()) {
            queryWrapper.eq("trade_sn", orderEntity.getTradeSn());
        } else if (null != orderEntity.getSn()) {
            queryWrapper.eq("sn", orderEntity.getSn());
        } else if (null != orderEntity.getId()) {
            queryWrapper.eq("id", orderEntity.getId());
        }
        return orderMapper.selectCount(queryWrapper);
    }
}
