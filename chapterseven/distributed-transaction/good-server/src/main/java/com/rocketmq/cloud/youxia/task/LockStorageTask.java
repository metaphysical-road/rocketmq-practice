package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.bo.SevenGoodBo;
import com.rocketmq.cloud.youxia.cache.LockStorageMessage;
import com.rocketmq.cloud.youxia.service.SevenGoodService;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class LockStorageTask {
    @Autowired
    private SevenGoodService sevenGoodService;

    @Autowired
    private LockStorageMessage lockStorageMessage;

    @Autowired
    private MessageListenerConcurrently messageListenerConcurrently;

    @Scheduled(fixedRate = 1000)
    public void consumerLockStorage() {
        SevenGoodBo sevenGoodBo=lockStorageMessage.getData();
        if(null!=sevenGoodBo){
            //执行锁定库存的操作
            sevenGoodService.lockInventory(sevenGoodBo);
        }
    }
}
