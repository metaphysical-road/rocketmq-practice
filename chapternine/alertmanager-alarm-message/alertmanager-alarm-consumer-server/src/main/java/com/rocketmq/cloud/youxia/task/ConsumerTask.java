package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.AlertManagerConsumerConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.ServiceState;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@EnableScheduling
@Component
public class ConsumerTask {
    @Autowired
    private AlertManagerConsumerConfig alertManagerConsumerConfig;

    private Map<String, DefaultMQPushConsumer> cacheConsumerClient=new ConcurrentHashMap<>();

    private Lock lock=new ReentrantLock();

    @Scheduled(fixedRate = 1000)
    public void onlineConsumeMessage() throws MQClientException {
        lock.lock();
        try {
            String onlineConsumerInstanceName = alertManagerConsumerConfig.getOnlineConsumerInstanceName();
            String[] arrays = null;
            if (onlineConsumerInstanceName.contains(",")) {
                arrays = onlineConsumerInstanceName.split(",");
            } else {
                arrays = new String[]{onlineConsumerInstanceName};
            }
            for (String s : arrays) {
                if (cacheConsumerClient.containsKey(s)) {
                    DefaultMQPushConsumer item = cacheConsumerClient.get(s);
                    if (item.getDefaultMQPushConsumerImpl().getServiceState().equals(ServiceState.SHUTDOWN_ALREADY)) {
                        item.start();
                    }
                    continue;
                }
                DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer(alertManagerConsumerConfig.getConsumerGroup());
                defaultMQPushConsumer.subscribe(alertManagerConsumerConfig.getTopic(), "");

                defaultMQPushConsumer.setConsumeThreadMin(alertManagerConsumerConfig.getConsumeThreadMin());
                defaultMQPushConsumer.setConsumeThreadMax(alertManagerConsumerConfig.getConsumeThreadMax());
                defaultMQPushConsumer.setConsumeMessageBatchMaxSize(alertManagerConsumerConfig.getConsumeMessageBatchMaxSize());
                defaultMQPushConsumer.setPullBatchSize(alertManagerConsumerConfig.getPullBatchSize());
                defaultMQPushConsumer.setPullThresholdForQueue(alertManagerConsumerConfig.getPullThresholdForQueue());
                defaultMQPushConsumer.setPullThresholdSizeForTopic(alertManagerConsumerConfig.getPullThresholdSizeForTopic());

                defaultMQPushConsumer.setInstanceName(s);
                defaultMQPushConsumer.setClientIP(alertManagerConsumerConfig.getClientIp());
                defaultMQPushConsumer.setNamesrvAddr(alertManagerConsumerConfig.getNameAddress());
                defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
                    @Override
                    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                        if (CollectionUtils.isNotEmpty(msgs)) {
                            for (MessageExt messageExt : msgs) {
//                                System.out.println("消费消息：" + messageExt.getMsgId());
                            }
                        }
                        if(alertManagerConsumerConfig.getSleepTime()>0){
                            try{
                                Thread.sleep(alertManagerConsumerConfig.getSleepTime());
                            }catch (InterruptedException e){}
                        }
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                });
                defaultMQPushConsumer.start();
                cacheConsumerClient.put(s, defaultMQPushConsumer);
            }
        }finally {
            lock.unlock();
        }
    }

    @Scheduled(fixedRate = 1000)
    public void offlineConsumeMessage(){
        if(alertManagerConsumerConfig.getOffline().equals("false")){
            return;
        }
        lock.lock();
        try {
            String offlineConsumerInstanceName = alertManagerConsumerConfig.getOfflineConsumerInstanceName();
            String[] arrays = null;
            if (offlineConsumerInstanceName.contains(",")) {
                arrays = offlineConsumerInstanceName.split(",");
            } else {
                arrays = new String[]{offlineConsumerInstanceName};
            }
            Map<String, DefaultMQPushConsumer> cloneCacheConsumerClient = new ConcurrentHashMap<>();
            Iterator<String> iterator = cacheConsumerClient.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                DefaultMQPushConsumer defaultMQPushConsumer = cacheConsumerClient.get(key);
                boolean isOffline = false;
                //遍历下线的消费者
                for (String s : arrays) {
                    //如果缓存中存在这个key，说明要将它剔除
                    if (key.equals(s)) {
                        isOffline = true;
                        break;
                    }
                }
                //遍历完成之后，不需要剔除，则重新缓存这个客户端
                if (!isOffline) {
                    cloneCacheConsumerClient.put(key, defaultMQPushConsumer);
                } else {
                    //释放资源
                    defaultMQPushConsumer.shutdown();
                }
            }
            cacheConsumerClient = cloneCacheConsumerClient;
        }finally {
            lock.unlock();
        }
    }
}
