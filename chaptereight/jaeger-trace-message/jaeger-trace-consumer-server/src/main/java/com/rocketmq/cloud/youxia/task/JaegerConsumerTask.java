package com.rocketmq.cloud.youxia.task;

import com.google.common.base.Charsets;
import com.rocketmq.cloud.youxia.config.ConsumerConfig;
import com.rocketmq.cloud.youxia.trace.TraceUtils;
import io.opentracing.Tracer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.trace.hook.ConsumeMessageOpenTracingHookImpl;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@EnableScheduling
@Component
public class JaegerConsumerTask {
    @Autowired
    private ConsumerConfig consumerConfig;

    @Autowired
    private TraceUtils traceUtils;

    private volatile AtomicBoolean startFlag= new AtomicBoolean(false);

    @Scheduled(fixedRate = 1000)
    public void consumerMessage() throws MQClientException {
        if (startFlag.compareAndSet(false, true)) {
            Tracer tracer = traceUtils.initTracer();
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer
                    (consumerConfig.getConsumerGroup());
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    if (CollectionUtils.isNotEmpty(msgs)) {
                        for (MessageExt messageExt : msgs) {
                            String content = new String(messageExt.getBody(), Charsets.UTF_8);
                            System.out.println("消费消息:" + content);
                        }
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            consumer.getDefaultMQPushConsumerImpl().registerConsumeMessageHook(new
                    ConsumeMessageOpenTracingHookImpl(tracer));
            consumer.setClientIP(consumerConfig.getCientIp());
            consumer.setNamesrvAddr(consumerConfig.getNamesrvAddr());
            consumer.setInstanceName(consumerConfig.getInstanceName()+ RandomUtils.nextLong(0,500000000));
            consumer.subscribe(consumerConfig.getTopic(), "");
            consumer.start();
            startFlag.set(true);
        }
    }
}
