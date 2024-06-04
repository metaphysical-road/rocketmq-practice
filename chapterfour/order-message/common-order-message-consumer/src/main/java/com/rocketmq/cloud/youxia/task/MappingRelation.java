package com.rocketmq.cloud.youxia.task;

import org.apache.rocketmq.common.message.MessageQueue;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MappingRelation {

    private Map<String, MessageQueue> mappingRelation = new ConcurrentHashMap<>();

    public synchronized void putValue(String key, MessageQueue messageQueue) {
        mappingRelation.put(key, messageQueue);
    }

    public  Map<String, MessageQueue> getMappingRelation() {
        return mappingRelation;
    }
}
