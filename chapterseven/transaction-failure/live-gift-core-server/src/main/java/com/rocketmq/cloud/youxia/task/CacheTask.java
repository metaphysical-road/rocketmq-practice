package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.entity.SevenLiveGiftEntity;
import com.rocketmq.cloud.youxia.manager.SevenLiveGiftManager;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@EnableScheduling
@Component
public class CacheTask {
    @Autowired
    private SevenLiveGiftManager sevenLiveGiftManager;

    @Autowired
    private GiftCachePool giftCachePool;

    @Scheduled(fixedRate = 60000)
    public void cache() {
        List<SevenLiveGiftEntity> cache = sevenLiveGiftManager.selectAll();
        if (CollectionUtils.isNotEmpty(cache)) {
            for (SevenLiveGiftEntity sevenLiveGiftEntity : cache) {
                if (!giftCachePool.getGiftIdCache().contains(sevenLiveGiftEntity.getId())) {
                    giftCachePool.putData(sevenLiveGiftEntity);
                }
            }
        }
    }
}
