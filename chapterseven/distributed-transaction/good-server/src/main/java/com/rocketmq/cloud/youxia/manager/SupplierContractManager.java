package com.rocketmq.cloud.youxia.manager;

import com.rocketmq.cloud.youxia.entity.SupplierAfterSaleEntity;
import com.rocketmq.cloud.youxia.entity.SupplierContractEntity;
import com.rocketmq.cloud.youxia.mapper.SupplierContractMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SupplierContractManager {
    @Resource
    private SupplierContractMapper supplierContractMapper;

    public Integer insert(SupplierContractEntity supplierContractEntity) {
        return supplierContractMapper.insert(supplierContractEntity);
    }
}
