package com.rocketmq.cloud.youxia.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.rocketmq.cloud.youxia.config.AccountConfig;
import com.rocketmq.cloud.youxia.entity.SevenAccountInfoEntity;
import com.rocketmq.cloud.youxia.manager.SevenAccountInfoManager;
import org.apache.commons.collections.CollectionUtils;
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

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

@EnableScheduling
@Component
public class ConsumerTask {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private SevenAccountInfoManager sevenAccountInfoManager;

    @Autowired
    private AccountConfig accountConfig;

    private volatile LongAdder longAdder=new LongAdder();

    @Scheduled(fixedRate = 1000)
    public void consumerMessage() throws MQClientException {
        while (longAdder.intValue()<accountConfig.getConsumerNum()){
            int add=accountConfig.getConsumerNum()-longAdder.intValue();
            for(int i=0;i<add;i++){
                DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer("giftMessage");
                defaultMQPushConsumer.subscribe("giftMessage", "");
                defaultMQPushConsumer.setNamesrvAddr("127.0.0.1:9876");
                defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
                    @Override
                    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                        if (CollectionUtils.isNotEmpty(msgs)) {
                            for (MessageExt messageExt : msgs) {
                                String content = new String(messageExt.getBody(), Charsets.UTF_8);
                                try {
                                    Map<String, Object> map = objectMapper.readValue(content, Map.class);
                                    if (map.containsKey("accountId") && map.containsKey("giveNum") &&
                                            map.containsKey("price")) {
                                        Long accountId=(Integer)map.get("accountId")+0L;
                                        Integer giveNum = (Integer)map.get("giveNum");
                                        Long price = (Integer)map.get("price")+0L;
                                        SevenAccountInfoEntity query = new SevenAccountInfoEntity();
                                        query.setId(accountId);
                                        SevenAccountInfoEntity fromDabase = sevenAccountInfoManager.select(query);
                                        Long balance=0L;
                                        if(fromDabase.getAmount()>=giveNum*price){
                                            balance = fromDabase.getAmount() -giveNum*price;
                                        }
                                        fromDabase.setAmount(balance);
                                        fromDabase.setGmt_modified(new Date(System.currentTimeMillis()));
                                        sevenAccountInfoManager.update(fromDabase);
                                    }
                                } catch (JsonProcessingException e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                        }
                        ConsumeConcurrentlyStatus result = ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        return result;
                    }
                });
                defaultMQPushConsumer.start();
                longAdder.increment();
            }
        }
    }
}
