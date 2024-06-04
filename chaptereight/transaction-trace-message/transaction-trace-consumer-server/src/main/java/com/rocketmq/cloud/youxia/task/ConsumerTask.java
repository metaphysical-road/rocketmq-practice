package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.TransactionTraceConsumerConfig;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@EnableScheduling
@Component
public class ConsumerTask {

    @Autowired
    private TransactionTraceConsumerConfig transactionTraceConsumerConfig;

    private volatile AtomicBoolean startFlag= new AtomicBoolean(false);

    @Scheduled(fixedRate = 1000)
    public void consumeMessage() throws MQClientException {
        if (startFlag.compareAndSet(false, true)) {
            DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer(transactionTraceConsumerConfig.getConsumerGroup()
                    , true, transactionTraceConsumerConfig.getTraceTopic());
            defaultMQPushConsumer.subscribe(transactionTraceConsumerConfig.getTopic(), "");
            defaultMQPushConsumer.setNamesrvAddr("127.0.0.1:9876");
            defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    if (CollectionUtils.isNotEmpty(msgs)) {
                        for (MessageExt messageExt : msgs) {
                            System.out.println("消费消息：" + messageExt.getMsgId());
                        }
                    }
                    try{
                        Thread.sleep(1000);
                    }catch (InterruptedException e){

                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            defaultMQPushConsumer.start();
            startFlag.set(true);
        }
    }
}
