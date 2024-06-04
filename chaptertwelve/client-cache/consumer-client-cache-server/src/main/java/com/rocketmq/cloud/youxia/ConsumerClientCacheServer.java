package com.rocketmq.cloud.youxia;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class ConsumerClientCacheServer {

    public static void main(String[] args){
        SpringApplication springApplication=new SpringApplication(ConsumerClientCacheServer.class);
        springApplication.run();
        ExecutorService executorService= Executors.newFixedThreadPool(1);
        ConsumerThread consumerThread=new ConsumerThread();
        consumerThread.setName("ConsumerCacheClientService:"+ RandomUtils.nextLong(1000,1000000000));
        executorService.execute(consumerThread);
    }

    static class ConsumerThread extends Thread {
        private DefaultMQPushConsumer defaultMQPushConsumer;
        private Object lock = new Object();

        @Override
        public void run() {
            try {
                synchronized (lock) {
                    if (null == defaultMQPushConsumer) {
                        defaultMQPushConsumer = new DefaultMQPushConsumer("testCacheConsumerClientGroup");
                        defaultMQPushConsumer.subscribe("testCacheProducerClientMessage", "");
                        defaultMQPushConsumer.setNamesrvAddr("127.0.0.1:9876");
                        defaultMQPushConsumer.setInstanceName(Thread.currentThread().getName() + ":" + RandomUtils.nextLong(1000, 100000000));
                        int port = Integer.parseInt(System.getProperty("server.port","8089"));                        defaultMQPushConsumer.setClientIP("127.0.0.1:" + port);
                        defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
                            @Override
                            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                                if (CollectionUtils.isNotEmpty(msgs)) {
                                    for (MessageExt messageExt : msgs) {
                                        System.out.println("Consumer message:" + messageExt.toString());
                                    }
                                }
                                ConsumeConcurrentlyStatus result = ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                                return result;
                            }
                        });
                        defaultMQPushConsumer.start();
                    }
                }
            } catch (MQClientException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
