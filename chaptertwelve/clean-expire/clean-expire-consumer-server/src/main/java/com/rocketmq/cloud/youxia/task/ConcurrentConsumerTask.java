package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.CleanExpireConcurrentConfig;
import com.rocketmq.cloud.youxia.listener.IMessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ConcurrentConsumerTask {

    private DefaultMQPushConsumer defaultMQPushConsumer;

    @Autowired
    private CleanExpireConcurrentConfig singleProcessConcurrentConfig;

    @Scheduled(fixedRate = 1000)
    public void consumeMessage() {
        try {
            if(singleProcessConcurrentConfig.getIsNeedModifyThreadNum().equals("true")){
                if(null!=defaultMQPushConsumer){
                    defaultMQPushConsumer.shutdown();
                    start();
                }
            }else if (null == defaultMQPushConsumer) {
                defaultMQPushConsumer = new DefaultMQPushConsumer(singleProcessConcurrentConfig.getConsumerGroup());
                start();
            }

        } catch (MQClientException e) {
            System.out.println(e.getCause().getMessage());
        }
    }

    private void start() throws MQClientException {
        defaultMQPushConsumer.subscribe(singleProcessConcurrentConfig.getTopic(), "");
        defaultMQPushConsumer.setNamesrvAddr(singleProcessConcurrentConfig.getNamesrvAddr());
        //设置消费消息的线程池的最小核心线程数
        defaultMQPushConsumer.setConsumeThreadMin(singleProcessConcurrentConfig.getConsumeThreadMin());
        //设置消费消息的线程池的最大核心线程数
        defaultMQPushConsumer.setConsumeThreadMax(singleProcessConcurrentConfig.getConsumeThreadMax());
        defaultMQPushConsumer.registerMessageListener(new IMessageListenerConcurrently());
        defaultMQPushConsumer.start();
    }
}
