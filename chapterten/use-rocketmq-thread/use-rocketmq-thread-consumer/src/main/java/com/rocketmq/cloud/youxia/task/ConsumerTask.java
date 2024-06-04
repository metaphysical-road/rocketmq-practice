package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.ConsumerConfig;
import com.rocketmq.cloud.youxia.listener.IMessageListenerConcurrently;
import com.rocketmq.cloud.youxia.thread.ConsumerThread;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@EnableScheduling
public class ConsumerTask {
    private List<DefaultMQPushConsumer> defaultMQPushConsumerList = new ArrayList<>();

    private Map<String, DefaultMQPushConsumer> defaultMQPushConsumerMap = new ConcurrentHashMap<>();

    public List<DefaultMQPushConsumer> getDefaultMQPushConsumerList() {
        return defaultMQPushConsumerList;
    }

    public Map<String, DefaultMQPushConsumer> getDefaultMQPushConsumerMap() {
        return defaultMQPushConsumerMap;
    }

    @Autowired
    private ConsumerConfig multiProcessConcurrentConfig;

    @Scheduled(fixedRate = 1000)
    public void consumeMessage() {
        String isNeedModifyThreadNum = multiProcessConcurrentConfig.getIsNeedModifyThreadNum();
        if (isNeedModifyThreadNum.equals("true")) {
            if (CollectionUtils.isNotEmpty(defaultMQPushConsumerList)) {
                for (DefaultMQPushConsumer defaultMQPushConsumer : defaultMQPushConsumerList) {
                    defaultMQPushConsumer.shutdown();
                }
            }
            defaultMQPushConsumerList.clear();
            defaultMQPushConsumerMap.clear();
        }
        String instanceName = multiProcessConcurrentConfig.getInstanceName();
        String[] instanceNameArray = instanceName.split(",");
        for (String s : instanceNameArray) {
            try {
                if (!defaultMQPushConsumerMap.containsKey(s)) {
                    if(multiProcessConcurrentConfig.getIsUseThread().equals("fasle")){
                        start(s, false);
                    }else{
                        ConsumerThread consumerThread=new ConsumerThread(
                                multiProcessConcurrentConfig,this, s,false
                        );
                        consumerThread.start();
                    }
                }
            } catch (MQClientException e) {
                System.out.println(e.getCause().getMessage());
            }
        }
    }

    private void start(String item,boolean isGroup) throws MQClientException {
        DefaultMQPushConsumer defaultMQPushConsumer = null;
        if (isGroup) {
            defaultMQPushConsumer = new DefaultMQPushConsumer(item);
            defaultMQPushConsumer.setInstanceName(multiProcessConcurrentConfig.getSingleInstanceName());
        } else {
            defaultMQPushConsumer = new DefaultMQPushConsumer(multiProcessConcurrentConfig.getConsumerGroup());
            defaultMQPushConsumer.setInstanceName(item);
        }
        defaultMQPushConsumer.setClientIP(multiProcessConcurrentConfig.getClientIp());
        defaultMQPushConsumer.subscribe(multiProcessConcurrentConfig.getTopic(), "");
        defaultMQPushConsumer.setNamesrvAddr(multiProcessConcurrentConfig.getNamesrvAddr());
        //设置消费消息的线程池的最小核心线程数
        defaultMQPushConsumer.setConsumeThreadMin(multiProcessConcurrentConfig.getConsumerThreadMin());
        //设置消费消息的线程池的最大核心线程数
        defaultMQPushConsumer.setConsumeThreadMax(multiProcessConcurrentConfig.getConsumerThreadMax());
        defaultMQPushConsumer.registerMessageListener(new IMessageListenerConcurrently());
        defaultMQPushConsumer.start();
        defaultMQPushConsumerList.add(defaultMQPushConsumer);
        defaultMQPushConsumerMap.put(defaultMQPushConsumer.getInstanceName(), defaultMQPushConsumer);
    }
}
