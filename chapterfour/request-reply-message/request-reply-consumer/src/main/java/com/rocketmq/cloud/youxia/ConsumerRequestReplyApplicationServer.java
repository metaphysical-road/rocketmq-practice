package com.rocketmq.cloud.youxia;

import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.consumer.TopicMessageQueueChangeListener;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.utils.MessageUtil;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
public class ConsumerRequestReplyApplicationServer {
    public static void main(String[] args){
        SpringApplication springApplication = new SpringApplication(ConsumerRequestReplyApplicationServer.class);
        springApplication.run();
        responseConsumerMessage();
    }

    static void responseConsumerMessage() {
        try {
            DefaultMQProducer replyProducer = new DefaultMQProducer("producerRequestReplyGroup");
            replyProducer.start();
            Map<String, Boolean> messageQueuqChange = new HashMap<>();
            String topic = "testAsyncSelectRequestReplyProducerMessage";
            DefaultLitePullConsumer defaultLitePullConsumer = new DefaultLitePullConsumer("testRequestReplyGroup");
            defaultLitePullConsumer.subscribe(topic, "");
            defaultLitePullConsumer.setNamesrvAddr("127.0.0.1:9876");
            defaultLitePullConsumer.start();
            defaultLitePullConsumer.registerTopicMessageQueueChangeListener(topic, new TopicMessageQueueChangeListener() {
                @Override
                public void onChanged(String topic, Set<MessageQueue> messageQueues) {
                    messageQueuqChange.put(topic, true);
                }
            });
            List<MessageExt> messageExts = defaultLitePullConsumer.poll();
            if (CollectionUtils.isNotEmpty(messageExts)) {
                for (MessageExt messageExt : messageExts) {
                    System.out.printf("handle message: %s", messageExt.toString());
                    String replyTo = MessageUtil.getReplyToClient(messageExt);
                    byte[] replyContent = "reply message contents.".getBytes();
                    Message replyMessage = MessageUtil.createReplyMessage(messageExt, replyContent);
                    SendResult replyResult = replyProducer.send(replyMessage, 3000);
                    System.out.printf("reply to %s , %s %n", replyTo, replyResult.toString());
                }
            }
            if (messageQueuqChange.get(topic)) {
                System.out.println("the message queue route info have changed!");
            }
        } catch (MQClientException e1) {
            System.out.println(e1.getMessage());
        } catch (MQBrokerException e2) {
            System.out.println(e2.getMessage());
        } catch (RemotingException e3) {
            System.out.println(e3.getMessage());
        } catch (InterruptedException e4) {
            System.out.println(e4.getMessage());
        }
    }
}
