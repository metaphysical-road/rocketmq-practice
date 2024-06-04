package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.DebugProducerConfig;
import org.apache.commons.lang3.RandomUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
@EnableScheduling
public class DebugProducerTask {

    private DefaultMQProducer mqProducer;

    @Autowired
    private DebugProducerConfig debugProducerConfig;

    @Scheduled(fixedRate = 200)
    public void producerMessage() throws MQClientException, MQBrokerException, InterruptedException
            , RemotingException, UnsupportedEncodingException {
        if(debugProducerConfig.getOpenProducer().equals("true")){
            String instanceName = "testDebugMessageModel" + RandomUtils.nextLong(100, 1000000000);
            if (null == mqProducer) {
                mqProducer = new DefaultMQProducer("testDebugMessageModel");
                //设置Name Server的IP地址
                mqProducer.setNamesrvAddr("127.0.0.1:9876");
                mqProducer.setInstanceName(instanceName);
                //启动生产者
                mqProducer.start();
            }
            String topic = "testDebugMessageModel";
            String message = "testDebugMessageModel" + RandomUtils.nextLong(100, 1000000000);
            Message msg = new Message(topic, (message).getBytes(RemotingHelper.DEFAULT_CHARSET));
            mqProducer.send(msg);
        }
    }
}
