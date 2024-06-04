package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.cache.DeadQueueCache;
import com.rocketmq.cloud.youxia.config.DeadQueueConsumerConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Component
@EnableScheduling
public class DeadQueueConsumerTask {
    private List<DefaultMQPushConsumer> defaultMQPushConsumerList = new ArrayList<>();

    private Map<String, DefaultMQPushConsumer> defaultMQPushConsumerMap = new ConcurrentHashMap<>();

    @Autowired
    private DeadQueueConsumerConfig deadQueueConsumerConfig;

    @Autowired
    @Qualifier(value = "iMessageListenerConcurrently")
    private MessageListenerConcurrently messageListenerConcurrentlyA;

    @Autowired
    @Qualifier(value = "deadQueueMessageListener")
    private MessageListenerConcurrently messageListenerConcurrentlyB;

    @Autowired
    private DeadQueueCache deadQueueCache;

    @Scheduled(fixedRate = 1000)
    public void consumeMessage() {
        String isSingleInstance = deadQueueConsumerConfig.getIsSingleInstance();
        if (isSingleInstance.equals("false")) {
            String instanceName = deadQueueConsumerConfig.getInstanceName();
            String[] instanceNameArray = instanceName.split(",");
            for (String s : instanceNameArray) {
                try {
                    if (!defaultMQPushConsumerMap.containsKey(s)) {
                        start(s, false);
                    }
                } catch (MQClientException e) {
                    System.out.println(e.getCause().getMessage());
                }
            }
        } else {
            String multiConsumerGroup = deadQueueConsumerConfig.getMultiConsumerGroup();
            String[] multiConsumerGroupArray = multiConsumerGroup.split(",");
            for (String consumerGroupName : multiConsumerGroupArray) {
                try {
                    if (!defaultMQPushConsumerMap.containsKey(consumerGroupName)) {
                        start(consumerGroupName, true);
                    }
                } catch (MQClientException e) {
                    System.out.println(e.getCause().getMessage());
                }
            }
        }
    }

    private void start(String item,boolean isGroup) throws MQClientException {
        DefaultMQPushConsumer defaultMQPushConsumer = null;
        if (isGroup) {
            defaultMQPushConsumer = new DefaultMQPushConsumer(item);
            defaultMQPushConsumer.setInstanceName(deadQueueConsumerConfig.getSingleInstanceName());
        } else {
            defaultMQPushConsumer = new DefaultMQPushConsumer(deadQueueConsumerConfig.getConsumerGroup());
            defaultMQPushConsumer.setInstanceName(item);
        }
        defaultMQPushConsumer.setClientIP(deadQueueConsumerConfig.getClientIp());
        defaultMQPushConsumer.subscribe(deadQueueConsumerConfig.getTopic(), "");
        defaultMQPushConsumer.setNamesrvAddr(deadQueueConsumerConfig.getNamesrvAddr());
        //设置消费消息的线程池的最小核心线程数
        defaultMQPushConsumer.setConsumeThreadMin(deadQueueConsumerConfig.getConsumerThreadMin());
        //设置消费消息的线程池的最大核心线程数
        defaultMQPushConsumer.setConsumeThreadMax(deadQueueConsumerConfig.getConsumerThreadMax());
        //从配置中心读取，设置消费者消费消息失败之后的最大重试次数
        defaultMQPushConsumer.setMaxReconsumeTimes(deadQueueConsumerConfig.getMaxReconsumeTimes());
        defaultMQPushConsumer.registerMessageListener(messageListenerConcurrentlyA);
        defaultMQPushConsumer.start();
        defaultMQPushConsumerList.add(defaultMQPushConsumer);
        defaultMQPushConsumerMap.put(defaultMQPushConsumer.getInstanceName(), defaultMQPushConsumer);
    }

    //消费死信队列中的消息
    @Scheduled(fixedRate = 1000)
    public void consumeDeadQueueMessage() throws MQClientException, ExecutionException {
        if(!defaultMQPushConsumerMap.containsKey(deadQueueConsumerConfig.getDeadInstanceName())){
            DefaultMQPushConsumer defaultMQPushConsumer=new DefaultMQPushConsumer();
            defaultMQPushConsumer.setConsumerGroup(deadQueueConsumerConfig.getDeadConsumerGroup());
            defaultMQPushConsumer.setClientIP(deadQueueConsumerConfig.getClientIp());
            defaultMQPushConsumer.setInstanceName(deadQueueConsumerConfig.getDeadInstanceName());
            defaultMQPushConsumer.subscribe("%DLQ%" + deadQueueConsumerConfig.getConsumerGroup(), "");
            defaultMQPushConsumer.setNamesrvAddr(deadQueueConsumerConfig.getNamesrvAddr());
            //设置消费消息的线程池的最小核心线程数
            defaultMQPushConsumer.setConsumeThreadMin(deadQueueConsumerConfig.getConsumerThreadMin());
            //设置消费消息的线程池的最大核心线程数
            defaultMQPushConsumer.setConsumeThreadMax(deadQueueConsumerConfig.getConsumerThreadMax());
            //从配置中心读取，设置消费者消费消息失败之后的最大重试次数
            defaultMQPushConsumer.setMaxReconsumeTimes(deadQueueConsumerConfig.getMaxReconsumeTimes());
            defaultMQPushConsumer.registerMessageListener(messageListenerConcurrentlyB);
            defaultMQPushConsumer.start();
            defaultMQPushConsumerList.add(defaultMQPushConsumer);
            defaultMQPushConsumerMap.put(defaultMQPushConsumer.getInstanceName(), defaultMQPushConsumer);
        }
    }
}
