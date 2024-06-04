package com.rocketmq.cloud.youxia;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class DynamicResetConsumerServer {
    public static void main(String[] args) throws MQClientException{
        SpringApplication springApplication=new SpringApplication(DynamicResetConsumerServer.class);
        springApplication.run();
        startConsumerInstance();
    }

    public static void startConsumerInstance() throws MQClientException {
        DefaultMQPushConsumer defaultMQPushConsumerOne = new DefaultMQPushConsumer("dynamiResetTest");
        defaultMQPushConsumerOne.setNamesrvAddr("127.0.0.1:9876");
        defaultMQPushConsumerOne.setInstanceName("clientIdOne");
        defaultMQPushConsumerOne.subscribe("dynamiResetTest", "");
        defaultMQPushConsumerOne.setClientIP("127.0.0.1:2000");

        defaultMQPushConsumerOne.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
//                System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        defaultMQPushConsumerOne.start();
    }
}
