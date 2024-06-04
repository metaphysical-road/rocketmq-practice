package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.MasterUpdateDledgerConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@EnableScheduling
public class MasterUpdateConsumerTask {

    private List<DefaultMQPushConsumer> defaultMQPushConsumerList = new ArrayList<>();

    private Map<String, DefaultMQPushConsumer> defaultMQPushConsumerMap = new ConcurrentHashMap<>();

    @Autowired
    private MasterUpdateDledgerConfig handleDledgerConsumerConfig;

    @Autowired
    private MessageListenerConcurrently messageListenerConcurrently;

    private Lock globalLock=new ReentrantLock();

    private volatile boolean isFirst=true;

    @Scheduled(fixedRate = 1000)
    public void consumeMessage() {
        globalLock.lock();
        try {
            String changeCluster = handleDledgerConsumerConfig.getChangeCluster();
            String moveMessageSuccess = handleDledgerConsumerConfig.getMoveMessageSuccess();
            String isSingleInstance = handleDledgerConsumerConfig.getIsSingleInstance();
            if (changeCluster.equals("true")) {
                if (isFirst == true) {
                    if (CollectionUtils.isNotEmpty(defaultMQPushConsumerList) &&
                            StringUtils.isNotEmpty(moveMessageSuccess) && moveMessageSuccess.equals("false")) {
                        for (DefaultMQPushConsumer defaultMQPushConsumer : defaultMQPushConsumerList) {
                            defaultMQPushConsumer.shutdown();
                        }
                        defaultMQPushConsumerList.clear();
                        defaultMQPushConsumerMap.clear();
                    }
                    isFirst = false;
                } else {
                    if (CollectionUtils.isEmpty(defaultMQPushConsumerList)) {
                        startConsumer(isSingleInstance);
                    }
                }
            } else {
                startConsumer(isSingleInstance);
            }
        } finally {
            globalLock.unlock();
        }
    }

    private void startConsumer(String isSingleInstance) {
        if (isSingleInstance.equals("false")) {
            String instanceName = handleDledgerConsumerConfig.getInstanceName();
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
            String multiConsumerGroup = handleDledgerConsumerConfig.getMultiConsumerGroup();
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
            defaultMQPushConsumer.setInstanceName(handleDledgerConsumerConfig.getSingleInstanceName());
        } else {
            defaultMQPushConsumer = new DefaultMQPushConsumer(handleDledgerConsumerConfig.getConsumerGroup());
            defaultMQPushConsumer.setInstanceName(item);
        }
        defaultMQPushConsumer.setClientIP(handleDledgerConsumerConfig.getClientIp());
        defaultMQPushConsumer.subscribe(handleDledgerConsumerConfig.getTopic(), "");
        if(handleDledgerConsumerConfig.getChangeCluster().equals("true")){
            defaultMQPushConsumer.setNamesrvAddr(handleDledgerConsumerConfig.getNewNamesrvAddr());
        }else{
            defaultMQPushConsumer.setNamesrvAddr(handleDledgerConsumerConfig.getNamesrvAddr());
        }
        //设置消费消息的线程池的最小核心线程数
        defaultMQPushConsumer.setConsumeThreadMin(handleDledgerConsumerConfig.getConsumerThreadMin());
        //设置消费消息的线程池的最大核心线程数
        defaultMQPushConsumer.setConsumeThreadMax(handleDledgerConsumerConfig.getConsumerThreadMax());
        //从配置中心读取，设置消费者消费消息失败之后的最大重试次数
        defaultMQPushConsumer.setMaxReconsumeTimes(handleDledgerConsumerConfig.getMaxReconsumeTimes());
        defaultMQPushConsumer.registerMessageListener(messageListenerConcurrently);
        defaultMQPushConsumer.start();
        defaultMQPushConsumerList.add(defaultMQPushConsumer);
        defaultMQPushConsumerMap.put(defaultMQPushConsumer.getInstanceName(), defaultMQPushConsumer);
    }
}
