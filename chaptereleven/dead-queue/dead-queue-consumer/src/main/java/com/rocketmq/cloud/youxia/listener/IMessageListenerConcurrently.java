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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class IMessageListenerConcurrently implements MessageListenerConcurrently {

    @Autowired
    private DeadQueueConsumerConfig deadQueueConsumerConfig;

    @Autowired
    private DeadQueueCache deadQueueCache;

    private Map<String,Integer> times=new ConcurrentHashMap<>();

    @Override
    public synchronized ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        ConsumeConcurrentlyStatus result = ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        try {
            if (CollectionUtils.isNotEmpty(msgs)) {
                String retryMessageIds = deadQueueConsumerConfig.getRetryMessageIds();
                List<String> messageIdList = new CopyOnWriteArrayList<>();
                String[] arrays = null;
                if (StringUtils.isNotEmpty(retryMessageIds)) {
                    if (retryMessageIds.contains(",")) {
                        arrays = retryMessageIds.split(",");
                    } else {
                        arrays = new String[]{retryMessageIds};
                    }
                }
                if (null != arrays) {
                    messageIdList = Arrays.asList(arrays);
                }
                List<String> messageIds = new CopyOnWriteArrayList<>();
                String topicName = "";
                for (MessageExt messageExt : msgs) {
                    if (messageIdList.contains(messageExt.getMsgId())) {
                        //动态读取延迟等级
                        context.setDelayLevelWhenNextConsume(
                                deadQueueConsumerConfig.getDelayLevelWhenNextConsume());
                        //触发重试队列，并重新消费
                        result = ConsumeConcurrentlyStatus.RECONSUME_LATER;
                        System.out.println("Consumer Message Fail!" + " 消息ID：" + messageExt.getMsgId() +
                                " 消息主题：" + messageExt.getTopic() + " Broker Server名称：" + messageExt.getBrokerName());
                        if (times.containsKey(messageExt.getMsgId())) {
                            Integer time = times.get(messageExt.getMsgId());
                            times.put(messageExt.getMsgId(), time++);
                        } else {
                            times.put(messageExt.getMsgId(), 1);
                        }
                        Integer total = times.get(messageExt.getMsgId());
                        if (total == deadQueueConsumerConfig.getMaxReconsumeTimes() + 1) {
                            messageIds.add(messageExt.getMsgId());
                        }
                        topicName = "%DLQ%" + deadQueueConsumerConfig.getConsumerGroup();
                    }
                    List<String> sourceMessageIds = deadQueueCache.
                            getDeadQueueMap().get(topicName);
                    if (CollectionUtils.isNotEmpty(sourceMessageIds)){
                        sourceMessageIds.addAll(messageIds);
                    }else{
                        deadQueueCache.getDeadQueueMap().put(topicName, messageIds);
                    }
                    //System.out.println("Consumer Message Success!:" + messageExt.toString());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result = ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
        return result;
    }
}
