package com.rocketmq.cloud.youxia.manager;

import com.rocketmq.cloud.youxia.entity.SupplierSettlementEntity;
import com.rocketmq.cloud.youxia.mapper.SupplierSettlementMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
public class SupplierSettlementManager {
    @Resource
    private SupplierSettlementMapper supplierSettlementMapper;
    public Integer insert(SupplierSettlementEntity supplierSettlementEntity){
        return supplierSettlementMapper.insert(supplierSettlementEntity);
    }
}
