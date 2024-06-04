package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.CustomHookConfig;
import com.rocketmq.cloud.youxia.dispatch.ConsumerAsyncTraceDispatcher;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.hook.ConsumeMessageHook;
import org.apache.rocketmq.client.trace.TraceDispatcher;
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
    private CustomHookConfig customHookConfig;

    @Autowired
    private ConsumerAsyncTraceDispatcher consumerAsyncTraceDispatcher;

    @Autowired
    private ConsumeMessageHook consumeMessageHook;

    private volatile AtomicBoolean startFlag= new AtomicBoolean(false);

    @Scheduled(fixedRate = 1000)
    public void consumeMessage() throws MQClientException {
        if (startFlag.compareAndSet(false, true)) {
            DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer(customHookConfig.getConsumerGroup());
            defaultMQPushConsumer.getDefaultMQPushConsumerImpl().registerConsumeMessageHook(consumeMessageHook);
            defaultMQPushConsumer.subscribe(customHookConfig.getTopic(), "");
            defaultMQPushConsumer.setNamesrvAddr(customHookConfig.getNameAddress());
            defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    if (CollectionUtils.isNotEmpty(msgs)) {
                        for (MessageExt messageExt : msgs) {
                            System.out.println("消费消息：" + messageExt.getMsgId());
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {

                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
            defaultMQPushConsumer.start();
            consumerAsyncTraceDispatcher.setHostConsumer(defaultMQPushConsumer.getDefaultMQPushConsumerImpl());
            consumerAsyncTraceDispatcher.start(customHookConfig.getNameAddress(),defaultMQPushConsumer.getAccessChannel());
            startFlag.set(true);
        }
    }
}
