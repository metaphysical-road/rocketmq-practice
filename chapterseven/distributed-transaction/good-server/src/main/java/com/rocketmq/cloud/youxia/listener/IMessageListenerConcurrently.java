package com.rocketmq.cloud.youxia.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.rocketmq.cloud.youxia.bo.SevenGoodBo;
import com.rocketmq.cloud.youxia.config.GoodConsumerConfig;
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
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class IMessageListenerConcurrently implements MessageListenerConcurrently {

    @Autowired
    private GoodConsumerConfig goodConsumerConfig;

    //消费消息
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        ConsumeConcurrentlyStatus result = ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        try {
            if (CollectionUtils.isNotEmpty(msgs)) {
                String retryMessageIds = goodConsumerConfig.getRetryMessageIds();
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
                for (MessageExt messageExt : msgs) {
                    if (messageIdList.contains(messageExt.getMsgId())) {
                        String content = new String(messageExt.getBody(), Charsets.UTF_8);
                        ObjectMapper objectMapper = new ObjectMapper();
                        Map<String, String> map = objectMapper.readValue(content, Map.class);
                        SevenGoodBo sevenGoodBo = new SevenGoodBo();
                        if (map.containsKey("processOnUuid")) {
                            Long processOnUuid = Long.valueOf(map.get("processOnUuid"));
                            sevenGoodBo.setParentUuid(processOnUuid);
                        }
                        if (map.containsKey("gobalUuid")) {
                            Long gobalUuid = Long.valueOf(map.get("gobalUuid"));
                            sevenGoodBo.setGobalUuid(gobalUuid);
                        }
                        if (map.containsKey("goodId")) {
                            Long goodId = Long.valueOf(map.get("goodId"));
                            sevenGoodBo.setGoodId(goodId);
                        }
                        if (map.containsKey("skuId")) {
                            String skuId = map.get("skuId");
                            sevenGoodBo.setSkuId(skuId);
                        }
                        if (map.containsKey("num")) {
                            Long num = Long.valueOf(map.get("num"));
                            sevenGoodBo.setNum(num.intValue());
                        }

                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result = ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
        return result;
    }
}
