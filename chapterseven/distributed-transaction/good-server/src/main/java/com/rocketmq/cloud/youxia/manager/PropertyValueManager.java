package com.rocketmq.cloud.youxia.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rocketmq.cloud.youxia.entity.PropertyNameEntity;
import com.rocketmq.cloud.youxia.entity.PropertyValueEntity;
import com.rocketmq.cloud.youxia.mapper.PropertyValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PropertyValueManager {
    @Resource
    private PropertyValueMapper propertyValueMapper;

    public Integer insert(PropertyValueEntity propertyValueEntity){
        return propertyValueMapper.insert(propertyValueEntity);
    }

    public PropertyValueEntity selectByName(String valueName){
        QueryWrapper<PropertyValueEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("value", valueName);
        return propertyValueMapper.selectOne(queryWrapper);
    }

    public Integer selectNumByName(String valueName){
        QueryWrapper<PropertyValueEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("value", valueName);
        return propertyValueMapper.selectCount(queryWrapper);
    }
}
