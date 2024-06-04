package com.rocketmq.cloud.youxia.listener;

import com.rocketmq.cloud.youxia.cache.DeadQueueCache;
import com.rocketmq.cloud.youxia.config.DeadQueueConsumerConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DeadQueueMessageListener implements MessageListenerConcurrently {

    @Autowired
    private DeadQueueConsumerConfig deadQueueConsumerConfig;

    @Autowired
    private DeadQueueCache deadQueueCache;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        ConsumeConcurrentlyStatus result = ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        try {
            if (CollectionUtils.isNotEmpty(msgs)) {
                for (MessageExt messageExt : msgs) {
                    synchronized (this) {
                        final String deadTopicName = "%DLQ%" + deadQueueConsumerConfig.getConsumerGroup();
                        List<String> messageIds = deadQueueCache.getDeadQueueMap().get(deadTopicName);
                        if (CollectionUtils.isNotEmpty(messageIds)) {
                            for (String messageId : messageIds) {
                                String consumeResult = deadQueueCache.getConsumerDeadQueueMap().get(messageId);
                                if (StringUtils.isNotEmpty(consumeResult)) {
                                    if (consumeResult.equals("true")) {
                                        System.out.println("重复消费! " + " 消息ID：" + messageExt.getMsgId() +
                                                " 消息主题：" + messageExt.getTopic() + " Broker Server名称：" + messageExt.getBrokerName());
                                        deadQueueCache.getConsumerDeadQueueMap().put(messageId,"true");
                                        continue;
                                    }
                                }else{
                                    System.out.println("Consumer Message Dead MessageQueue Success!" + " 消息ID：" + messageExt.getMsgId() +
                                            " 消息主题：" + messageExt.getTopic() + " Broker Server名称：" + messageExt.getBrokerName());
                                    deadQueueCache.getConsumerDeadQueueMap().put(messageId,"true");
                                }
                            }
                        }else{
                            List<String> undoConsumerMessageIds=new ArrayList<>();
                            undoConsumerMessageIds.add(messageExt.getMsgId());
                            deadQueueCache.getDeadQueueMap().put(deadTopicName,undoConsumerMessageIds);
                            deadQueueCache.getConsumerDeadQueueMap().put(messageExt.getMsgId(),"true");
                            System.out.println("Consumer Message Dead MessageQueue Success!" + " 消息ID：" + messageExt.getMsgId() +
                                    " 消息主题：" + messageExt.getTopic() + " Broker Server名称：" + messageExt.getBrokerName());
                            deadQueueCache.getConsumerDeadQueueMap().put(messageExt.getMsgId(),"true");
                        }
                    }
                }
            }
        }catch (ExecutionException e){}
        return result;
    }
}
