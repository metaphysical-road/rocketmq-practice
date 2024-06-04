package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.ProducerConfig;
import org.apache.commons.lang3.RandomUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;

@Component
@EnableScheduling
public class ProducerTask {

    private DefaultMQProducer defaultMQProducer;

    @Resource
    private ProducerConfig producerConfig;

    @Scheduled(fixedRate = 20000)
    public void producerMessage() {
        if (null == defaultMQProducer) {
            defaultMQProducer = new DefaultMQProducer(producerConfig.getProducerGroupName());
            defaultMQProducer.setNamesrvAddr(producerConfig.getNamesrvAddr());
            defaultMQProducer.setInstanceName(producerConfig.getInstanceName());
            defaultMQProducer.setClientIP(producerConfig.getClientIp());
            defaultMQProducer.setDefaultTopicQueueNums(producerConfig.getTopicQueueNums());
//            defaultMQProducer.setUnitName(producerConfig.getUnitName());
            try {
                defaultMQProducer.start();
            } catch (MQClientException e) {
                System.out.println(e.getMessage());
            }
        }
        try {
            //生产消息
            String topic = producerConfig.getTopic();
            //构造消息体
            Message msg = new Message(topic,
                    ("this is a test message" + RandomUtils.nextLong(1, 20000000)).
                            getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult result = defaultMQProducer.send(msg);
            if (result.getSendStatus().equals(SendStatus.SEND_OK)) {
                System.out.println("生产消息成功，消息ID为:" + result.getMsgId());
            }
        } catch (UnsupportedEncodingException e1) {
            System.out.println(e1.getMessage());
        } catch (MQClientException e2) {
            System.out.println(e2.getMessage());
        } catch (RemotingException e3) {
            System.out.println(e3.getMessage());
        } catch (MQBrokerException e4) {
            System.out.println(e4.getMessage());
        } catch (InterruptedException e5) {
            System.out.println(e5.getMessage());
        }
    }
}
