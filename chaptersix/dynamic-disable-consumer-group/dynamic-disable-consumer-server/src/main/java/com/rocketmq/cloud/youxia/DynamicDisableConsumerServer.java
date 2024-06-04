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
public class DynamicDisableConsumerServer {
    public static void main(String[] args) throws MQClientException{
        SpringApplication springApplication=new SpringApplication(DynamicDisableConsumerServer.class);
        springApplication.run();
        startConsumerInstanceA();
        startConsumerInstanceB();
        startConsumerInstanceC();
    }

    public static void startConsumerInstanceA() throws MQClientException {
        DefaultMQPushConsumer defaultMQPushConsumerOne=new DefaultMQPushConsumer("disableConsumerTest");
        defaultMQPushConsumerOne.setNamesrvAddr("127.0.0.1:9876");
        defaultMQPushConsumerOne.setInstanceName("clientIdOne");
        defaultMQPushConsumerOne.subscribe("disableConsumerTest","");
        defaultMQPushConsumerOne.setClientIP("127.0.0.1:2000");

        defaultMQPushConsumerOne.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        defaultMQPushConsumerOne.start();
    }

    public static void startConsumerInstanceB() throws MQClientException{
        DefaultMQPushConsumer defaultMQPushConsumerTwo=new DefaultMQPushConsumer("disableConsumerTest");
        defaultMQPushConsumerTwo.setNamesrvAddr("127.0.0.1:9876");
        defaultMQPushConsumerTwo.setInstanceName("clientIdTwo");
        defaultMQPushConsumerTwo.subscribe("disableConsumerTest","");
        defaultMQPushConsumerTwo.setClientIP("127.0.0.1:2001");

        defaultMQPushConsumerTwo.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        defaultMQPushConsumerTwo.start();
    }

    public static void startConsumerInstanceC() throws MQClientException{
        DefaultMQPushConsumer defaultMQPushConsumerThree=new DefaultMQPushConsumer("disableConsumerTest");
        defaultMQPushConsumerThree.setNamesrvAddr("127.0.0.1:9876");
        defaultMQPushConsumerThree.setInstanceName("clientIdThree");
        defaultMQPushConsumerThree.subscribe("disableConsumerTest","");
        defaultMQPushConsumerThree.setClientIP("127.0.0.1:2002");
        defaultMQPushConsumerThree.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        defaultMQPushConsumerThree.start();
    }
}
