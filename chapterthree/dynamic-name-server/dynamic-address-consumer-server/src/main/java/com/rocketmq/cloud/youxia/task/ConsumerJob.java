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

@Component
@EnableScheduling
public class ConsumerJob {

    @Autowired
    private ConsumerConfig consumerConfig;

    private DefaultLitePullConsumer defaultLitePullConsumer;

    private DefaultLitePullConsumer addressPullConsumer;

    @Scheduled(cron = "*/5 * * * * ?")
    public void consumerMessage() throws MQClientException {
        if (consumerConfig.getIsOpenAddressServer().equals("true")) {
            Map<String, Boolean> messageQueuqChange = new HashMap<>();
            String topic = "dynamicAddressServer";
            if (null == addressPullConsumer) {
                addressPullConsumer = new DefaultLitePullConsumer("dynamicAddressServer");
                addressPullConsumer.subscribe(topic, "");
                addressPullConsumer.start();
                addressPullConsumer.registerTopicMessageQueueChangeListener(topic, new TopicMessageQueueChangeListener() {
                    @Override
                    public void onChanged(String topic, Set<MessageQueue> messageQueues) {
                        messageQueuqChange.put(topic, true);
                    }
                });
            }
            consumeMessage(messageQueuqChange, topic, addressPullConsumer);
        } else {
            Map<String, Boolean> messageQueuqChange = new HashMap<>();
            String topic = "dynamicAddressServer";
            if (null == defaultLitePullConsumer) {
                defaultLitePullConsumer = new DefaultLitePullConsumer("dynamicAddressServer");
                defaultLitePullConsumer.subscribe(topic, "");
                defaultLitePullConsumer.setNamesrvAddr("127.0.0.1:9876");
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
