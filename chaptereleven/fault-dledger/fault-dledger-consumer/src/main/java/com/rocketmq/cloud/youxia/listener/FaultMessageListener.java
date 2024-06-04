package com.rocketmq.cloud.youxia.listener;

import com.rocketmq.cloud.youxia.config.FaultDledgerConsumerConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class FaultMessageListener implements MessageListenerConcurrently {

    @Autowired
    private FaultDledgerConsumerConfig faultDledgerConsumerConfig;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        ConsumeConcurrentlyStatus result = ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        try {
            if (CollectionUtils.isNotEmpty(msgs)) {
                String retryMessageIds=faultDledgerConsumerConfig.getRetryMessageIds();
                List<String> messageIdList=new CopyOnWriteArrayList<>();
                String[] arrays=null;
                if(StringUtils.isNotEmpty(retryMessageIds)){
                    if(retryMessageIds.contains(",")){
                        arrays=retryMessageIds.split(",");
                    }else{
                        arrays=new String[]{retryMessageIds};
                    }
                }
                if(null!=arrays){
                    messageIdList= Arrays.asList(arrays);
                }
                for (MessageExt messageExt : msgs) {
                    if(messageIdList.contains(messageExt.getMsgId())){
                        //动态读取延迟等级
                        context.setDelayLevelWhenNextConsume(
                                faultDledgerConsumerConfig.getDelayLevelWhenNextConsume());
                        //触发重试队列，并重新消费
                        result = ConsumeConcurrentlyStatus.RECONSUME_LATER;
                        System.out.println("Consumer Message Fail!" + " 消息ID："+messageExt.getMsgId()+
                                " 消息主题："+messageExt.getTopic()+" Broker Server名称："+messageExt.getBrokerName());
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
