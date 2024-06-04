package com.rocketmq.cloud.youxia;

import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.TopicMessageQueueChangeListener;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
public class MutiConsumerMessageApplicationServer{
    public static void main(String[] args) throws MQClientException{
        SpringApplication springApplication = new SpringApplication(MutiConsumerMessageApplicationServer.class);
        springApplication.run();
        //testPullConsumerMessage();
        testPushConsumerMessage();
        //testPopConsumerMessage();
    }

    static void testPullConsumerMessage() throws MQClientException {
        Map<String,Boolean> messageQueuqChange=new HashMap<>();
        String topic="pullConsumerOrder";
        DefaultLitePullConsumer defaultLitePullConsumer = new DefaultLitePullConsumer("testPullConsumerGroup");
        defaultLitePullConsumer.subscribe(topic, "");
        defaultLitePullConsumer.setNamesrvAddr("127.0.0.1:9876");
        defaultLitePullConsumer.start();
        defaultLitePullConsumer.registerTopicMessageQueueChangeListener(topic, new TopicMessageQueueChangeListener() {
            @Override
            public void onChanged(String topic, Set<MessageQueue> messageQueues) {
                messageQueuqChange.put(topic,true);
            }
        });
        List<MessageExt> messageExts = defaultLitePullConsumer.poll();
        if(CollectionUtils.isNotEmpty(messageExts)){
            for(MessageExt messageExt:messageExts){
                System.out.println("consume pull message "+messageExt.toString());
            }
        }
        if(messageQueuqChange.get(topic)){
            System.out.println("the message queue route info have changed!");
        }
    }

    static void testPushConsumerMessage() throws MQClientException {
        DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer("testPushConsumerGroup");
        defaultMQPushConsumer.subscribe("testSyncDefaultProducerMessage", "");
        defaultMQPushConsumer.setNamesrvAddr("127.0.0.1:9876");
        defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                if (CollectionUtils.isNotEmpty(msgs)) {
                    for (MessageExt messageExt : msgs) {
//                        System.out.println("Consumer message:" + messageExt.toString());
                    }
                }
                ConsumeConcurrentlyStatus result=ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                return result;
            }
        });
        defaultMQPushConsumer.start();
    }

    static void testPopConsumerMessage() throws MQClientException {
        DefaultMQPushConsumer defaultMQPopConsumer = new DefaultMQPushConsumer("testPopConsumerGroup");
        defaultMQPopConsumer.subscribe("popConsumerOrder", "");
        defaultMQPopConsumer.setNamesrvAddr("127.0.0.1:9876");
        defaultMQPopConsumer.setClientRebalance(false);
        defaultMQPopConsumer.getDefaultMQPushConsumerImpl().setConsumeOrderly(false);
        defaultMQPopConsumer.setMessageModel(MessageModel.CLUSTERING);
        defaultMQPopConsumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            if (CollectionUtils.isNotEmpty(msgs)) {
                for (MessageExt messageExt : msgs) {
                    System.out.println("Consumer message:" + messageExt.toString());
                }
            }
            ConsumeConcurrentlyStatus result=ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            return result;
        });
        defaultMQPopConsumer.start();
    }
}
