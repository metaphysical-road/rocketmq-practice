package com.rocketmq.cloud.youxia.task;

import com.google.common.base.Charsets;
import com.rocketmq.cloud.youxia.config.ProducerConfig;
import org.apache.commons.lang3.RandomUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@EnableScheduling
@Component
public class ProducerTask {
    @Autowired
    private ProducerConfig producerConfig;

    private DefaultMQProducer defaultMQProducer;

    @Scheduled(fixedRate = 20000)
    public void producerMessage() throws MQClientException,InterruptedException, UnsupportedEncodingException,
            RemotingException, MQBrokerException {
        if (null == defaultMQProducer) {
            //定义一个生产者对象mqProducer
            defaultMQProducer = new DefaultMQProducer(producerConfig.getProducerGroup());
            //设置Name Server的IP地址
            defaultMQProducer.setNamesrvAddr(producerConfig.getNameAddress());
            defaultMQProducer.setInstanceName(producerConfig.getInstanceName());
            defaultMQProducer.setClientIP(producerConfig.getClientIp());
            defaultMQProducer.setSendMsgTimeout(2000);
            defaultMQProducer.setRetryTimesWhenSendAsyncFailed(2);
            defaultMQProducer.setRetryTimesWhenSendFailed(2);
            //启动生产者
            defaultMQProducer.start();
        }
        Integer batchSize=producerConfig.getBatchSize();
        long sleepTime=producerConfig.getSleepTime();
        for (int i = 0; i < batchSize; i++) {
            //定义消息主题名称
            String topic = producerConfig.getTopic();
            //构造消息体
            Message msg = new Message(topic,
                    ("this is a test message" + RandomUtils.nextLong(1, 20000000)).
                            getBytes(RemotingHelper.DEFAULT_CHARSET));
            String content = new String(msg.getBody(), Charsets.UTF_8);
            msg.getProperties().put("delay","true");
            msg.getProperties().put("delayTime",producerConfig.getDelayTime()+"");
            SendResult result = defaultMQProducer.send(msg);
            String msgId = result.getMsgId();
            String offsetMsgId = result.getOffsetMsgId();
//            System.out.println("msgId:" + msgId + " offsetMsgId:" + offsetMsgId);
        }
        Thread.sleep(sleepTime);
    }
}
