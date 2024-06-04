package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.MultiProcessConcurrentConfig;
import com.rocketmq.cloud.youxia.listener.IMessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@EnableScheduling
public class DynamicModifyThreadTask {

    @Scheduled(fixedRate = 1000)
    public void consumeMessage() {
        if(multiProcessConcurrentConfig.getConsumerType().equals("push")){
            startNewConsumer(true);
        }else{
            startNewConsumer(false);
        }
        //下线消费者客户端
        if(multiProcessConcurrentConfig.getIsOffline().equals("true"))
        {
            String offlineInstance = multiProcessConcurrentConfig.getOfflineInstance();
            String[] offlineInstanceArray = null;
            if (offlineInstance.contains(",")) {
                offlineInstanceArray = offlineInstance.split(",");
            } else {
                offlineInstanceArray = new String[]{offlineInstance};
            }
            for (String s : offlineInstanceArray) {
                if (defaultMQPushConsumerMap.containsKey(s)) {
                    DefaultMQPushConsumer item = defaultMQPushConsumerMap.get(s);
                    item.shutdown();
                    defaultMQPushConsumerMap.remove(s);
                    ;
                    defaultMQPushConsumerList.remove(item);
                }
            }
        }
    }

    private void startNewConsumer(boolean isPush) {
        String instanceName = multiProcessConcurrentConfig.getInstanceName();
        String[] instanceNameArray = instanceName.split(",");
        for (String s : instanceNameArray) {
            try {
                if (!defaultMQPushConsumerMap.containsKey(s)) {
                    if (multiProcessConcurrentConfig.getIsOffline()
                            .equals("true") && multiProcessConcurrentConfig.getOfflineInstance().contains(s)) {
                        //跳过
                    } else {
                        start(s,isPush);
                    }
                }
            } catch (MQClientException e) {
                System.out.println(e.getCause().getMessage());
            }
        }
    }

    private void start(String item,boolean isPush) throws MQClientException {
        if(isPush){
            DefaultMQPushConsumer defaultMQPushConsumer = null;
            defaultMQPushConsumer = new DefaultMQPushConsumer(multiProcessConcurrentConfig.getConsumerGroup());
            defaultMQPushConsumer.setInstanceName(item);
            defaultMQPushConsumer.setConsumerGroup(multiProcessConcurrentConfig.getConsumerGroup());
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
        }else{

        }
    }

    private List<DefaultMQPushConsumer> defaultMQPushConsumerList = new ArrayList<>();

    private Map<String, DefaultMQPushConsumer> defaultMQPushConsumerMap = new ConcurrentHashMap<>();

    private List<DefaultLitePullConsumer> defaultMQPullConsumerList = new ArrayList<>();

    private Map<String, DefaultLitePullConsumer> defaultMQPullConsumerMap = new ConcurrentHashMap<>();

    @Autowired
    private MultiProcessConcurrentConfig multiProcessConcurrentConfig;
}
