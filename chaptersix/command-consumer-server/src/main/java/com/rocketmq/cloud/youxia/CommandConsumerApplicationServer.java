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
public class CommandConsumerApplicationServer {
    public static void main(String[] args)  throws MQClientException{
        SpringApplication.run(CommandConsumerApplicationServer.class);
        startOneConsumerInstance();
        startTwoConsumerInstance();
        startThreeConsumerInstance();
    }

    public static void startOneConsumerInstance() throws MQClientException{
        DefaultMQPushConsumer defaultMQPushConsumerOne=new DefaultMQPushConsumer("defaultConsumerGroup");
        defaultMQPushConsumerOne.setNamesrvAddr("127.0.0.1:9876");
        defaultMQPushConsumerOne.setInstanceName("clientIdOne");
        defaultMQPushConsumerOne.subscribe("managementInformationSystem","");
        defaultMQPushConsumerOne.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        defaultMQPushConsumerOne.start();
    }

    public static void startTwoConsumerInstance() throws MQClientException{
        DefaultMQPushConsumer defaultMQPushConsumerTwo=new DefaultMQPushConsumer("defaultConsumerGroup");
        defaultMQPushConsumerTwo.setNamesrvAddr("127.0.0.1:9876");
        defaultMQPushConsumerTwo.setInstanceName("clientIdTwo");
        defaultMQPushConsumerTwo.subscribe("managementInformationSystem","");
        defaultMQPushConsumerTwo.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        defaultMQPushConsumerTwo.start();
    }

    public static void startThreeConsumerInstance() throws MQClientException{
        DefaultMQPushConsumer defaultMQPushConsumerThree=new DefaultMQPushConsumer("defaultConsumerGroup");
        defaultMQPushConsumerThree.setNamesrvAddr("127.0.0.1:9876");
        defaultMQPushConsumerThree.setInstanceName("clientIdThree");
        defaultMQPushConsumerThree.subscribe("managementInformationSystem","");
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
