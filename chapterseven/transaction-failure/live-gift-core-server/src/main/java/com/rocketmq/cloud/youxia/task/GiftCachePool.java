package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.entity.SevenLiveGiftEntity;
import org.apache.commons.collections.list.SynchronizedList;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class GiftCachePool {
    private static Map<Long, SevenLiveGiftEntity> cachePool = new ConcurrentHashMap<>();
    private static List<Long> giftIdCache = SynchronizedList.decorate(new CopyOnWriteArrayList());

    public List<Long> getGiftIdCache() {
        return giftIdCache;
    }

    public void putData(SevenLiveGiftEntity item) {
        cachePool.put(item.getId(), item);
        giftIdCache.add(item.getId());
    }

    public synchronized SevenLiveGiftEntity randomGift() {
        Integer index = RandomUtils.nextInt(0, giftIdCache.size());
        return cachePool.get(giftIdCache.get(index));
    }
}
