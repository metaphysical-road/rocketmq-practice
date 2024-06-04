package com.rocketmq.cloud.youxia.task;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;

@Component
@EnableScheduling
public class MappingRelationTask {

    @Autowired
    private MappingRelation mappingRelation;

    @Scheduled(cron = "*/60 * * * * ?")
    public void printMappingRelation() throws MQClientException {
        System.out.println("定时器开始打印映射关系");
        if (mappingRelation.getMappingRelation().size() > 0) {
            Map<String, MessageQueue> items = mappingRelation.getMappingRelation();
            Iterator<String> stringIterator = items.keySet().iterator();
            while (stringIterator.hasNext()) {
                String key = stringIterator.next();
                MessageQueue messageQueue = items.get(key);
                System.out.println("消费消息的映射关系：" + key);
            }
        }
    }
}
