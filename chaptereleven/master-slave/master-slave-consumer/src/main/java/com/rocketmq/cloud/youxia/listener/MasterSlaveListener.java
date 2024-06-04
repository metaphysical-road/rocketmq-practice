package com.rocketmq.cloud.youxia.listener;

import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class MasterSlaveListener implements MessageListenerConcurrently {
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        ConsumeConcurrentlyStatus result=ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        if(CollectionUtils.isNotEmpty(msgs)){
            for(MessageExt messageExt:msgs){
                System.out.println("Consumer Message Success!" + " 消息ID：" + messageExt.getMsgId() +
                        " 消息主题：" + messageExt.getTopic() + " Broker Server名称：" + messageExt.getBrokerName());
            }
        }
        return result;
    }
}
