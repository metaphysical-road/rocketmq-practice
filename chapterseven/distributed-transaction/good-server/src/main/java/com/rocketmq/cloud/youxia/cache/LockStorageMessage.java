package com.rocketmq.cloud.youxia.cache;

import com.rocketmq.cloud.youxia.bo.SevenGoodBo;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class LockStorageMessage {
    private static Queue<SevenGoodBo> cacheQueue = new ConcurrentLinkedQueue();

    public synchronized void putData(SevenGoodBo sevenGoodBo) {
        cacheQueue.add(sevenGoodBo);
    }

    public synchronized SevenGoodBo getData() {
        return cacheQueue.poll();
    }
}
