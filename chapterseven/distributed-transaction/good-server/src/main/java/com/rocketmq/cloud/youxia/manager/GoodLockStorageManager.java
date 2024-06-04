package com.rocketmq.cloud.youxia.manager;

import com.rocketmq.cloud.youxia.entity.GoodLockStorageEntity;
import com.rocketmq.cloud.youxia.mapper.GoodLockStorageMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class GoodLockStorageManager {
    @Resource
    private GoodLockStorageMapper goodLockStorageMapper;

    public Integer insertGoodLockStorage(GoodLockStorageEntity goodLockStorageEntity){
        return goodLockStorageMapper.insert(goodLockStorageEntity);
    }
}
