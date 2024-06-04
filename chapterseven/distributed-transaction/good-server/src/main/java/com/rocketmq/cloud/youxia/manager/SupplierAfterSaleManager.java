package com.rocketmq.cloud.youxia.manager;

import com.rocketmq.cloud.youxia.entity.SupplierAfterSaleEntity;
import com.rocketmq.cloud.youxia.mapper.SupplierAfterSaleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SupplierAfterSaleManager {
    @Resource
    private SupplierAfterSaleMapper supplierAfterSaleMapper;
    public Integer insert(SupplierAfterSaleEntity supplierAfterSaleEntity){
        return supplierAfterSaleMapper.insert(supplierAfterSaleEntity);
    }
}
