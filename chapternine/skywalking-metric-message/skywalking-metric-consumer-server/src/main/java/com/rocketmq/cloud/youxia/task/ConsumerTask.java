package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.ConsumerConfig;
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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@EnableScheduling
@Component
public class ConsumerTask {
    @Autowired
    private ConsumerConfig consumerConfig;

    private Map<String, DefaultMQPushConsumer> cacheConsumerClient=new ConcurrentHashMap<>();

    private Lock lock=new ReentrantLock();

    @Scheduled(fixedRate = 1000)
    public void onlineConsumeMessage() throws MQClientException {
        lock.lock();
        try {
            String onlineConsumerInstanceName = consumerConfig.getOnlineConsumerInstanceName();
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
                DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer(consumerConfig.getConsumerGroup());
                defaultMQPushConsumer.subscribe(consumerConfig.getTopic(), "");
                defaultMQPushConsumer.setInstanceName(s);
                defaultMQPushConsumer.setClientIP(consumerConfig.getClientIp());
                defaultMQPushConsumer.setNamesrvAddr(consumerConfig.getNameAddress());
                defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
                    @Override
                    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                        if (CollectionUtils.isNotEmpty(msgs)) {
                            for (MessageExt messageExt : msgs) {
//                                System.out.println("消费消息：" + messageExt.getMsgId());
                            }
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
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
}
