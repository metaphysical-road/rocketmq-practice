package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.ConsumerConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.consumer.TopicMessageQueueChangeListener;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;

@Component
@EnableScheduling
public class ConsumerMessageTask {
    private DefaultLitePullConsumer defaultLitePullConsumer;

    @Autowired
    private ConsumerConfig consumerConfig;
    private volatile LongAdder longAdder=new LongAdder();

    @Scheduled(cron = "*/5 * * * * ?")
    public void consume() throws MQClientException {
        if (consumerConfig.getIsUseNewConsumer().equals("true") && longAdder.intValue() <= 20) {
            longAdder.increment();
            Map<String, Boolean> messageQueuqChange = new HashMap<>();
            String topic = "statelessNameServer";
            DefaultLitePullConsumer newLitePullConsumer = new DefaultLitePullConsumer("statelessNameServer");
            newLitePullConsumer.subscribe(topic, "");
            newLitePullConsumer.setNamesrvAddr("127.0.0.1:9876;127.0.0.1:9877;127.0.0.1:9878");
            newLitePullConsumer.setInstanceName("statelessNameServer" + longAdder.intValue());
            newLitePullConsumer.setClientIP("127.0.0.1:235" + longAdder.intValue());
            newLitePullConsumer.start();
            newLitePullConsumer.registerTopicMessageQueueChangeListener(topic, new TopicMessageQueueChangeListener() {
                @Override
                public void onChanged(String topic, Set<MessageQueue> messageQueues) {
                    messageQueuqChange.put(topic, true);
                }
            });
            consumeMessage(messageQueuqChange, topic, newLitePullConsumer);
        } else {
            Map<String, Boolean> messageQueuqChange = new HashMap<>();
            String topic = "statelessNameServer";
            if (null == defaultLitePullConsumer) {
                defaultLitePullConsumer = new DefaultLitePullConsumer("statelessNameServer");
                defaultLitePullConsumer.subscribe(topic, "");
                defaultLitePullConsumer.setNamesrvAddr("127.0.0.1:9876");
                defaultLitePullConsumer.setInstanceName("statelessNameServer");
                defaultLitePullConsumer.start();
                defaultLitePullConsumer.registerTopicMessageQueueChangeListener(topic, new TopicMessageQueueChangeListener() {
                    @Override
                    public void onChanged(String topic, Set<MessageQueue> messageQueues) {
                        messageQueuqChange.put(topic, true);
                    }
                });
            }
            consumeMessage(messageQueuqChange, topic, defaultLitePullConsumer);
        }
    }

    private void consumeMessage(Map<String, Boolean> messageQueuqChange, String topic, DefaultLitePullConsumer addressPullConsumer) {
        List<MessageExt> messageExts = addressPullConsumer.poll();
        if (CollectionUtils.isNotEmpty(messageExts)) {
            for (MessageExt messageExt : messageExts) {
                System.out.println("consume pull message " + messageExt.toString());
            }
        }
        if (messageQueuqChange.containsKey(topic)&&messageQueuqChange.get(topic)) {
            System.out.println("the message queue route info have changed!");
        }
    }
}
