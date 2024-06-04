package com.rocketmq.cloud.youxia.manager;

import com.rocketmq.cloud.youxia.entity.SupplierEntity;
import com.rocketmq.cloud.youxia.mapper.SupplierMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SupplierManager {
    @Resource
    private SupplierMapper supplierMapper;

    private Integer insert(SupplierEntity supplierEntity){
        return supplierMapper.insert(supplierEntity);
    }
}
