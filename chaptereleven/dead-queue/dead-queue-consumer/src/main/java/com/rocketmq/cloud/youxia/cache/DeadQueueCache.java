package com.rocketmq.cloud.youxia.cache;

import com.google.common.base.Ticker;
import com.google.common.cache.*;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;
import java.util.List;
@Component
public class DeadQueueCache {

    private LoadingCache<String, List<String>> deadQueueMap = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .concurrencyLevel(10)
            .recordStats()
            .ticker(Ticker.systemTicker())
            .removalListener(new RemovalListener<Object, Object>() {
                @Override
                public void onRemoval(RemovalNotification<Object, Object> notification) {
                    System.out.println(notification.getKey() + " was removed, cause is " + notification.getCause());
                }
            })
            .build(
                    new CacheLoader<String, List<String>>() {
                        @Override
                        public List<String> load(String key) {
                            List<String> list = Lists.newArrayList();
                            return list;
                        }
                    }
            );

    private LoadingCache<String, String> consumerDeadQueueMap = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .concurrencyLevel(10)
            .recordStats()
            .ticker(Ticker.systemTicker())
            .removalListener(new RemovalListener<Object, Object>() {
                @Override
                public void onRemoval(RemovalNotification<Object, Object> notification) {
                    System.out.println(notification.getKey() + " was removed, cause is " + notification.getCause());
                }
            })
            .build(
                    new CacheLoader<String, String>() {
                        @Override
                        public String load(String key) {
                            return "";
                        }
                    }
            );

    public LoadingCache<String, List<String>> getDeadQueueMap() {
        return deadQueueMap;
    }

    public LoadingCache<String,String> getConsumerDeadQueueMap(){
        return consumerDeadQueueMap;
    }
}
