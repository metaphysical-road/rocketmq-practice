package com.rocketmq.cloud.youxia.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rocketmq.cloud.youxia.entity.BrandEntity;
import com.rocketmq.cloud.youxia.entity.PropertyNameEntity;
import com.rocketmq.cloud.youxia.mapper.PropertyNameMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PropertyNameManager {
    @Resource
    private PropertyNameMapper propertyNameMapper;

    public Integer insert(PropertyNameEntity propertyNameEntity){
        return propertyNameMapper.insert(propertyNameEntity);
    }

    public PropertyNameEntity selectByName(String propertyName){
        QueryWrapper<PropertyNameEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", propertyName);
        return propertyNameMapper.selectOne(queryWrapper);
    }

    public Integer selectNumByName(String propertyName){
        QueryWrapper<PropertyNameEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", propertyName);
        return propertyNameMapper.selectCount(queryWrapper);
    }
}
