package com.rocketmq.cloud.youxia.task;

import org.apache.commons.lang3.RandomUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.topic.TopicValidator;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
@EnableScheduling
public class ProducerTask {

    @Scheduled(fixedRate = 2000)
    public void producerMessage() throws MQClientException, UnsupportedEncodingException {
        //定义一个生产者对象mqProducer
        DefaultMQProducer mqProducer = new DefaultMQProducer("scheduleGroup");
        //设置Name Server的IP地址
        mqProducer.setNamesrvAddr("127.0.0.1:9876");
        //启动生产者
        mqProducer.start();
        //定义消息主题名称
        String topic= "scheduleMessage";
        //构造消息体
        Message msg = new Message(topic,
                ("This is sync default pattern test message" + RandomUtils.nextLong(1, 20000000)).
                        getBytes(RemotingHelper.DEFAULT_CHARSET));
        //设置延迟等级
        msg.setDelayTimeLevel(1);
    }
}
