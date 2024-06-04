package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.FaultToleranceConfig;
import com.rocketmq.cloud.youxia.listener.IMessageListenerConcurrently;
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

    @Autowired
    private FaultToleranceConfig faultToleranceConfig;

    @Scheduled(fixedRate = 1000)
    public void consumeMessage() {
        String isSingleInstance = faultToleranceConfig.getIsSingleInstance();
        if (isSingleInstance.equals("false")) {
            String instanceName = faultToleranceConfig.getInstanceName();
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
            String multiConsumerGroup = faultToleranceConfig.getMultiConsumerGroup();
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
            defaultMQPushConsumer.setInstanceName(faultToleranceConfig.getSingleInstanceName());
        } else {
            defaultMQPushConsumer = new DefaultMQPushConsumer(faultToleranceConfig.getConsumerGroup());
            defaultMQPushConsumer.setInstanceName(item);
        }
        defaultMQPushConsumer.setClientIP(faultToleranceConfig.getClientIp());
        defaultMQPushConsumer.subscribe(faultToleranceConfig.getTopic(), "");
        defaultMQPushConsumer.setNamesrvAddr(faultToleranceConfig.getNamesrvAddr());
        //设置消费消息的线程池的最小核心线程数
        defaultMQPushConsumer.setConsumeThreadMin(faultToleranceConfig.getConsumerThreadMin());
        //设置消费消息的线程池的最大核心线程数
        defaultMQPushConsumer.setConsumeThreadMax(faultToleranceConfig.getConsumerThreadMax());
        defaultMQPushConsumer.registerMessageListener(new IMessageListenerConcurrently());
        defaultMQPushConsumer.start();
        defaultMQPushConsumerList.add(defaultMQPushConsumer);
        defaultMQPushConsumerMap.put(defaultMQPushConsumer.getInstanceName(), defaultMQPushConsumer);
    }
}
