package com.rocketmq.cloud.youxia;

import com.rocketmq.cloud.youxia.pool.ConsumerCenterPool;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ConsumerMessageJob implements SimpleJob {
    @Autowired
    private DynamicConfig dynamicConfig;
    @Autowired
    private ConsumerCenterPool consumerCenterPool;

    @Override
    public void execute(ShardingContext shardingContext) {
        String topicName = dynamicConfig.getTopicName();
        if(StringUtils.isNotEmpty(topicName)){
            if(topicName.contains(";")){
                consumerBatchTopicMessage(topicName);
            }else{
                consumerMessage(topicName);
            }
        }else{
            System.out.println("topic name is null!");
        }
    }

    private void consumerBatchTopicMessage(String topicName) {
        String [] topicArray= topicName.split(";");
        for(String name:topicArray){
            consumerMessage(name);
        }
    }

    private void consumerMessage(String topic) {
        Map<String, DefaultMQPushConsumer> defaultMQConsumerMap = consumerCenterPool.getDefaultMQConsumer(topic);
        Iterator<String> iterator = defaultMQConsumerMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            DefaultMQPushConsumer defaultMQPushConsumer = defaultMQConsumerMap.get(key);
            AtomicBoolean consumerStatus = consumerCenterPool.getMqConsumerStatus().get(key);
            if (null != consumerStatus && consumerStatus.equals(true)) {
                //说明这个消费者已经启动了，不能重复启动
                System.out.println("消费者不能重复启动：" + key);
            } else if (null != consumerStatus && consumerStatus.equals(false)) {
                //说明这个消费者已经被剔除了，不能启动
                System.out.println("消费者已经被剔除：" + key);
            } else {
                try {
                    defaultMQPushConsumer.start();
                    consumerCenterPool.updateConsumerStatus(key, new AtomicBoolean(true));
                } catch (MQClientException mqClientException) {
                    System.out.println(mqClientException.getMessage());
                }
            }
        }
    }
}
