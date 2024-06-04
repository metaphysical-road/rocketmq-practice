package com.rocketmq.cloud.youxia.task;

import com.google.common.base.Charsets;
import com.rocketmq.cloud.youxia.config.StrictConsumerConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
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
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@EnableScheduling
public class StrictConsumerTask {

    @Autowired
    private StrictConsumerConfig consumerConfig;

    private Map<String, DefaultMQPushConsumer> cacheDefaultMQPushConsumer = new ConcurrentHashMap<>();

    private Map<String, AtomicBoolean> consumerStatus = new ConcurrentHashMap<>();

    private volatile LongAdder longAdder = new LongAdder();

    @Autowired
    private MappingRelation mappingRelation;

    private Lock lock = new ReentrantLock();

    @Scheduled(cron = "*/5 * * * * ?")
    public void consumerMessage() throws MQClientException {
        try {
            lock.lock();
            Integer consumerClientNum = consumerConfig.getConsumerClientNum();
            while (longAdder.intValue() < consumerClientNum) {
                String instanceName = "testStrictOrderMessage" + RandomUtils.nextInt(0, 1000000000);
                while (cacheDefaultMQPushConsumer.containsKey(instanceName)) {
                    instanceName = "testStrictOrderMessage" + RandomUtils.nextInt(0, 1000000000);
                }
                final String clientIp = "127.0.0.1:676" + longAdder.intValue();
                final String consumerGroup = "testStrictOrderMessage";
                final String topicName = "testStrictOrderMessage";
                DefaultMQPushConsumer defaultMQPushConsumer = createConsumer(instanceName, clientIp, consumerGroup, topicName);
                cacheDefaultMQPushConsumer.put(defaultMQPushConsumer.getInstanceName(), defaultMQPushConsumer);
                longAdder.increment();
            }
            Iterator<String> iterator = cacheDefaultMQPushConsumer.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                DefaultMQPushConsumer value = cacheDefaultMQPushConsumer.get(key);
                if (!consumerStatus.containsKey(key) || consumerStatus.get(key).equals(false)) {
                    value.start();
                    consumerStatus.put(key, new AtomicBoolean(true));
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private synchronized DefaultMQPushConsumer createConsumer(String instanceName, String clientIp,
                                                              String consumerGroup, String topicName) throws MQClientException {
        DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer(consumerGroup);
        defaultMQPushConsumer.subscribe(topicName, "");
        defaultMQPushConsumer.setNamesrvAddr("127.0.0.1:9876");
        defaultMQPushConsumer.setInstanceName(instanceName);
        defaultMQPushConsumer.setClientIP(clientIp);
        defaultMQPushConsumer.setConsumeThreadMax(1);
        defaultMQPushConsumer.setConsumeThreadMin(1);
        defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                final DefaultMQPushConsumer copyDefaultMQPushConsumer = defaultMQPushConsumer;
//                System.out.println(copyDefaultMQPushConsumer.getDefaultMQPushConsumerImpl().getmQClientFactory().getClientId()+"开始消费消息!");
                if (CollectionUtils.isNotEmpty(msgs)) {
                    for (MessageExt messageExt : msgs) {
                        System.out.println(copyDefaultMQPushConsumer.getInstanceName() + "_" + context.getMessageQueue().getQueueId() + "_" + context.getMessageQueue().getBrokerName() +
                                "_" + new String(messageExt.getBody(), Charsets.UTF_8));
                    }
                }
                ConsumeConcurrentlyStatus result = ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                mappingRelation.putValue(copyDefaultMQPushConsumer.getDefaultMQPushConsumerImpl().getmQClientFactory().getClientId() + "_" + context.getMessageQueue().getQueueId()
                        + "_" + context.getMessageQueue().getBrokerName() + "_" + context.getMessageQueue().getTopic(), context.getMessageQueue());
//                System.out.println(copyDefaultMQPushConsumer.getDefaultMQPushConsumerImpl().getmQClientFactory().getClientId()+"消费消息结束!");
                return result;
            }
        });
        return defaultMQPushConsumer;
    }
}
