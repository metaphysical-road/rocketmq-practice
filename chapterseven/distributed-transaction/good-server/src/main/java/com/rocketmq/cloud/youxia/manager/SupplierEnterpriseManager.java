package com.rocketmq.cloud.youxia.manager;

import com.rocketmq.cloud.youxia.entity.SupplierEnterpriseEntity;
import com.rocketmq.cloud.youxia.mapper.SupplierEnterpriseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SupplierEnterpriseManager {
    @Resource
    private SupplierEnterpriseMapper supplierEnterpriseMapper;

    public Integer insert(SupplierEnterpriseEntity supplierEnterpriseEntity) {
        return supplierEnterpriseMapper.insert(supplierEnterpriseEntity);
    }
}
