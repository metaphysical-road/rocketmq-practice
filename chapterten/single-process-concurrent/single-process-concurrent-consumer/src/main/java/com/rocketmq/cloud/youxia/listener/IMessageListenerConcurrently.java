package com.rocketmq.cloud.youxia.listener;

import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

public class IMessageListenerConcurrently implements MessageListenerConcurrently {
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        ConsumeConcurrentlyStatus result = ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        try {
            if (CollectionUtils.isNotEmpty(msgs)) {
                for (MessageExt messageExt : msgs) {
                    System.out.println("Consumer message:" + messageExt.toString());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result = ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
        return result;
    }
}
