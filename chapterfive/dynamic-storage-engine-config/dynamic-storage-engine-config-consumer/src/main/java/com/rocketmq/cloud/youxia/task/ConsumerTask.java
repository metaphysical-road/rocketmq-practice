package com.rocketmq.cloud.youxia.task;

import com.google.common.base.Charsets;
import com.rocketmq.cloud.youxia.config.DynamicStorageEngineConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@EnableScheduling
public class ConsumerTask {
    @Autowired
    private DynamicStorageEngineConfig dynamicStorageEngineConfig;

    private Map<String, DefaultMQPushConsumer> consumerOn=new ConcurrentHashMap<>();

    private volatile AtomicBoolean isOpen=new AtomicBoolean(false);

    @Scheduled(fixedRate = 200)
    public void consumerMessage() throws MQClientException {
        if (dynamicStorageEngineConfig.getOpenConsumer().equals("true") && isOpen.compareAndSet(false, true)) {
            DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer(dynamicStorageEngineConfig.getConsumerGroup());
            defaultMQPushConsumer.subscribe(dynamicStorageEngineConfig.getTopic(), "");
            defaultMQPushConsumer.setNamesrvAddr(dynamicStorageEngineConfig.getNamesrvAddr());
            defaultMQPushConsumer.setInstanceName(dynamicStorageEngineConfig.getInstanceName());
            defaultMQPushConsumer.setClientIP(dynamicStorageEngineConfig.getClientIP());
            defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    final DefaultMQPushConsumer copyDefaultMQPushConsumer = defaultMQPushConsumer;
                    if (CollectionUtils.isNotEmpty(msgs)) {
                        for (MessageExt messageExt : msgs) {
                            System.out.println(copyDefaultMQPushConsumer.getInstanceName() + "_" + context.getMessageQueue().getQueueId() + "_" + context.getMessageQueue().getBrokerName() +
                                    "_" + new String(messageExt.getBody(), Charsets.UTF_8));
                        }
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            defaultMQPushConsumer.start();
            consumerOn.put(defaultMQPushConsumer.getInstanceName(), defaultMQPushConsumer);
            isOpen.set(true);
        } else {
            if (consumerOn.size() > 0 && dynamicStorageEngineConfig.getOpenConsumer().equals("false")) {
                Iterator<String> iterator = consumerOn.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    DefaultMQPushConsumer needStop = consumerOn.get(key);
                    needStop.shutdown();
                    consumerOn.remove(needStop);
                }
            }
        }

    }
}
